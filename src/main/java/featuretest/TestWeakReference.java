package featuretest;

import java.lang.ref.WeakReference;

import java.lang.ref.WeakReference;


class Car {
    private double price;
    private String colour;

    public Car(double price, String colour) {
        this.price = price;
        this.colour = colour;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String toString() {
        return colour + "car costs $" + price;
    }

}


/**
 * @author wison
 */
public class TestWeakReference {


//    public static void main(String[] args) {

//        Car car = new Car(22000, "silver");
//        WeakReference<Car> weakCar = new WeakReference<Car>(car);
//        int i = 0;
//        while (true) {
//            if (weakCar.get() != null) {
//                i++;
//                System.out.println("Object is alive for " + i + " loops - " + weakCar);
//            } else {
//                System.out.println("Object has been collected.");
//                break;
//            }
//        }
//    }


//    weak reference指向的object就不会被回收了. 因为还有一个strong reference car 指向它.

//    ReferenceQueue
//    在weak reference指向的对象被回收后, weak reference本身其实也就没有用了. java提供了一个ReferenceQueue来保存这些所指向的对象已经被回收的reference. 用法是在定义WeakReference的时候将一个ReferenceQueue的对象作为参数传入构造函数.

    public static void main(String[] args) {

        Car car = new Car(22000,"silver");
        WeakReference<Car> weakCar = new WeakReference<Car>(car);

        int i=0;

        while(true){
//            System.out.println("here is the strong reference 'car' "+car);
            if(weakCar.get()!=null){
                i++;
                System.out.println("Object is alive for "+i+" loops - "+weakCar);
            }else{
                System.out.println("Object has been collected.");
                break;
            }
        }
    }

}