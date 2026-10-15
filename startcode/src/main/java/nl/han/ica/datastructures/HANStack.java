package nl.han.ica.datastructures;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
    private List<T> list;

    public  HANStack() {
        this.list = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        this.list.add(value);
    }

    @Override
    public T pop() {
        if(list.isEmpty()){
            throw new EmptyStackException();
        }
        return list.remove(list.size()-1);
    }

    @Override
    public T peek() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size()-1);
    }
}
