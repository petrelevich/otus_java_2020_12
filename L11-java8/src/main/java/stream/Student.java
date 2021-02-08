package stream;


class Student {
    private final String name;
    private final int age;
    private final int course;
    private final double avgMark;

    Student(String name, int age, int course, double avgMark) {
        this.name = name;
        this.age = age;
        this.course = course;
        this.avgMark = avgMark;
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    int getCourse() {
        return course;
    }

    double getAvgMark() {
        return avgMark;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", course=" + course +
                ", avgMark=" + avgMark +
                '}';
    }
}
