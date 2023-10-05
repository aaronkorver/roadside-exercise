package com.example.geico.roadside.stateMachine;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    public StateMachineTransition toAssigned = new StateMachineTransition(AssistantState.ASSIGNED, AssistantState.AVAILABLE, "toAssigned");
    public StateMachineTransition toAvailable = new StateMachineTransition(AssistantState.AVAILABLE, AssistantState.ASSIGNED, "toAvailable");

    public Map<AssistantState,StateMachineTransition> transitionMap = new HashMap<>();

    {
        transitionMap.put(AssistantState.ASSIGNED,toAvailable);
        transitionMap.put(AssistantState.AVAILABLE,toAssigned);
    }
    public boolean canTransition(AssistantState fromState, AssistantState toState) {
        return transitionMap.get(fromState).canTransitionTo(toState);
    }
}
