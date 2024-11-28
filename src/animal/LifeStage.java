package animal;

public enum LifeStage {
    ADULT(3),
    CHILD(0);


    private LifeStage(int age) {
        this.age = age;
    }

    private final int age;

    public int getAge() {
        return age;
    }
}
