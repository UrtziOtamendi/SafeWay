package otamendi.urtzi.com.safeway.Utils;




// You could do it as well generic, that's what I do in my lib:
public interface SimpleCallback<T> {
    void callback(T data);
}