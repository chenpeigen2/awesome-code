package entity;

public class Test<T> {
    private T data;

    public void setData(T t) {
        data = t;
        System.out.println("mmp" + "查看设置结果: " + data);
    }

    public T getData() {
        return data;
    }

}