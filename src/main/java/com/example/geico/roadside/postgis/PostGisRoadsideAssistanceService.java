package com.example.geico.roadside.postgis;

import com.example.geico.roadside.Assistant;
import com.example.geico.roadside.Customer;
import com.example.geico.roadside.Geolocation;
import com.example.geico.roadside.RoadsideAssistanceService;
import com.example.geico.roadside.stateMachine.AssistantState;
import com.example.geico.roadside.stateMachine.AssistantTransaction;
import com.example.geico.roadside.stateMachine.AssistantTransactionRepository;
import com.example.geico.roadside.stateMachine.StateMachine;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostGisRoadsideAssistanceService implements RoadsideAssistanceService {

    final AssistantRepository assistantRepository;
    final AssistantTransactionRepository transactionRepository;

    TransactionTemplate transactionTemplate;

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
    private final StateMachine stateMachine = new StateMachine();

    public PostGisRoadsideAssistanceService(AssistantRepository assistantRepository,
                                            AssistantTransactionRepository transactionRepository,
                                            PlatformTransactionManager transactionManager) {
        this.assistantRepository = assistantRepository;
        this.transactionRepository = transactionRepository;
        transactionTemplate = new TransactionTemplate(transactionManager);
    }



    @Override
    public void updateAssistantLocation(Assistant assistant, Geolocation assistantLocation) {
        assistantRepository.findAssistantModelByName(assistant.getName()).ifPresentOrElse(
            assistantModel -> {
                AssistantModel newData = new AssistantModel(
                        assistantModel.getId(),
                        assistantModel.getName(),
                        assistantLocation.getLat(),
                        assistantLocation.getLon(),
                        factory.createPoint(new Coordinate(assistantLocation.getLon(),assistantLocation.getLat()))
                );
                assistantRepository.save(newData);
                System.out.printf("Updated %s to lat %s lon %s", newData.getName(), newData.getLat(), newData.getLon());
            },
                () -> System.out.println("Couldn't find the assistant with name " + assistant.getName())
        );

    }

    @Override
    public List<Assistant> findNearestAssistants(Geolocation geolocation, int limit) {
        Point p = factory.createPoint(new Coordinate(geolocation.getLon(),geolocation.getLat()));
        return assistantRepository
                .findNearest(p,limit)
                .stream()
                .map(assistantModel -> new Assistant(
                        assistantModel.getId(),
                        assistantModel.getName(),
                        new Geolocation(assistantModel.getLat(),assistantModel.getLon())))
                .toList();
    }

    @Override
    public Optional<Assistant> reserveAssistant(Customer customer, Geolocation geolocation) {
        List<Assistant> nearest = findNearestAssistants(geolocation,10);
        // for each assistant attempt to reserve one until success stop looping once succeeded
        for (Assistant assistant : nearest){
            try {
                transitionAssistant(assistant, AssistantState.ASSIGNED, customer);
                //note, as soon as we have no exceptions from the transition we immediately break the loop by returning
                return Optional.of(assistant);
            }
            catch (Exception ignored){ //means we were not able to assign, move on to the next Assistant
                }
        }
        return Optional.empty();
    }

    @Override
    public void releaseAssistant(Customer customer, Assistant assistant) {
        Optional<AssistantTransaction> current =
                transactionRepository.findAssistantTransactionByAssistantIdAndMostRecentTrue(assistant.getId());
        current.ifPresentOrElse(
                transaction -> {
                    if(transaction.getCustomerId().equals(customer.getId())) {
                        transitionAssistant(assistant, AssistantState.AVAILABLE, customer);
                    }
                    else{
                        throw new IllegalArgumentException("Can't release this assistant as it is for the wrong customer");
                    }
                },
                () -> {
                    throw new IllegalArgumentException("Could not find assistant to release");
                }
        );

    }


    /**
     * This is to handle the concurrency of assigning the same assistant to different customers
     * Transaction begins --> find existing transaction,  if exists: check if we can move to the new state, we attempt to update it and insert new one.
     * If the record is already being updated, then DB constraints fail us and we know another thread grabbed it.
     * If persisting succeeds, we are done. --> Commit Transaction.
     * If we don't find an existing transaction, then either something else is updating already or first state
     * @param assistant
     * @param toState
     * @param customer
     */
    private void transitionAssistant(Assistant assistant, AssistantState toState, Customer customer){
        transactionTemplate.executeWithoutResult(
                status -> {
                    Optional<AssistantTransaction> current =
                            transactionRepository.findAssistantTransactionByAssistantIdAndMostRecentTrue(assistant.getId());
                    current.ifPresentOrElse(
                            assistantTransaction -> {
                                //update old transaction (also locks the record)
                                assistantTransaction.setMostRecent(false);
                                assistantTransaction.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                transactionRepository.saveAndFlush(assistantTransaction);

                                //check if we can actually reserve this Assistant
                                if(stateMachine.canTransition(assistantTransaction.getToState(), toState))
                                {
                                    AssistantModel assistantModel = new AssistantModel(assistant.getId(),assistant.getName(),0.0,0.0,null);
                                    //insert new one
                                    AssistantTransaction newTransaction = new AssistantTransaction(assistantModel,customer.getId(),toState,true);
                                    transactionRepository.save(newTransaction);
                                }
                                else {
                                    throw new IllegalStateException("Can't transition from " + assistantTransaction.getToState() + " to " + toState);
                                }
                            },
                            () -> {
                                //insert new transaction only if state is going to ASSIGNED
                                if(toState.equals(AssistantState.ASSIGNED)) {
                                    AssistantModel assistantModel = new AssistantModel(assistant.getId(),assistant.getName(),0.0,0.0,null);
                                    //insert new one
                                    AssistantTransaction newTransaction = new AssistantTransaction(assistantModel,customer.getId(),toState,true);
                                    transactionRepository.save(newTransaction);
                                }
                                else {
                                    throw new IllegalStateException("Can't move to Available without first being assigned once.");
                                }
                            }
                    );
                }
        );

    }
}
