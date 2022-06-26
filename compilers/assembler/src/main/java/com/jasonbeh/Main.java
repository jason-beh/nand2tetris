package main.java.com.jasonbeh;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0){
            Parser parser = new Parser(args[0]);
        } else {
            System.out.println("Please supply file");
        }

    }
}
