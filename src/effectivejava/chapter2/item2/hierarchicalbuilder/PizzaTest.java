package src.effectivejava.chapter2.item2.hierarchicalbuilder;

import static src.effectivejava.chapter2.item2.hierarchicalbuilder.NyPizza.Size.SMALL;
import static src.effectivejava.chapter2.item2.hierarchicalbuilder.Pizza.Topping.*;

// Using the hierarchical builder (Page 16)
public class PizzaTest {
    public static void main(String[] args) {
        NyPizza pizza = new NyPizza.Builder(SMALL)
                .addTopping(SAUSAGE).addTopping(ONION).build();
        Calzone calzone = new Calzone.Builder()
                .addTopping(HAM).sauceInside().build();

        System.out.println(pizza);
        System.out.println(calzone);
    }
}
