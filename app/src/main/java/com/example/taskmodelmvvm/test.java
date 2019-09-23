package com.example.taskmodelmvvm;

import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class test {

    public abstract int move();
    public int nekiInt() {
        return move();
    }


}

class A extends test {
    @Override
    public int move() {
        return 13;
    }
}

class B extends test {

    @Override
    public int move() {
        return 11;
    }
}

class User {

    void primjer() {
        B b = new B();
        A a = new A();
        List<test> tests = new ArrayList<>();
        tests.add(a);
        tests.add(b);
        tests.add(new test() {
            @Override
            public int move() {
                return 3;
            }
        });
        for (test test: tests) {
            test.nekiInt();
        }


    }
}
