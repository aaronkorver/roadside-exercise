package com.example.geico.roadside.stateMachine;

public class StateMachineTransition {
    private AssistantState to;
    private AssistantState from;

    private String transitionName;

    public StateMachineTransition(AssistantState to, AssistantState from, String transitionName) {
        this.to = to;
        this.from = from;
        this.transitionName = transitionName;
    }

    public boolean canTransitionTo(AssistantState toState){
        return to.equals(toState);
    }

    public String getTransitionName() {
        return transitionName;
    }
}
