package jdkversions.jdk16;

import java.util.Objects;

record RecordsDemo(int x, int y) {
    // Implicitly declared fields
//    private final int x;
//    private final int y;

    // Other implicit declarations elided ...

    // Implicitly declared canonical constructor
    RecordsDemo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// the java 4 knights
class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int x() {
        return x;
    }

    int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

//record Point(int x, int y) {
//
//    // add sth to the constructor
//    Point {
//        int yy = 12;
//    }
//
//}

class PointF {
    public PointF() {

    }
}

class App {
    public static void main(String[] args) {
        new Point(1, 2);
        // constructor is a very ordinary method ,without a litter ting
    }
}