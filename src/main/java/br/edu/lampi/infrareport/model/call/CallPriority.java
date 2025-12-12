package br.edu.lampi.infrareport.model.call;

public enum CallPriority {
    Urgent(0),
    High(1),
    Medium(2),
    Low(3),
    No(4);

    private int priority;

    CallPriority(int priority) {
        this.priority = priority;
    }
    public int getPriority(){
        return this.priority;
    }
}
