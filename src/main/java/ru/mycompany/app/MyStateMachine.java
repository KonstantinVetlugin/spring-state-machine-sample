package ru.mycompany.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;


import java.util.EnumSet;

public class MyStateMachine {
    static enum States {
        STATE1, STATE2
    }

    static enum Events {
        EVENT1, EVENT2
    }

    @Configuration
    @EnableStateMachine
    static class Config1 extends EnumStateMachineConfigurerAdapter<States, Events> {

        @Override
        public void configure(StateMachineStateConfigurer<States, Events> states)
                throws Exception {
            states
                    .withStates()
                    .initial(States.STATE1)
                    .states(EnumSet.allOf(States.class));
        }

        @Override
        public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
                throws Exception {
            transitions
                    .withExternal()
                    .source(States.STATE1).target(States.STATE2)
                    .event(Events.EVENT1)
                    .and()
                    .withExternal()
                    .source(States.STATE2).target(States.STATE1)
                    .event(Events.EVENT2);
        }
    }

    @WithStateMachine
    static class MyBean {

        @OnTransition(target = "STATE1")
        void toState1() {
        }

        @OnTransition(target = "STATE2")
        void toState2() {
        }
    }

    static class MyApp {

        @Autowired
        StateMachine<States, Events> stateMachine;

        void doSignals() {
            stateMachine.start();
            stateMachine.sendEvent(Events.EVENT1);
            stateMachine.sendEvent(Events.EVENT2);
        }
        public static void main(String[] args){
            ApplicationContext context = new AnnotationConfigApplicationContext(Config1.class);

            MyApp myApp = context.getBean(MyApp.class);
            myApp.doSignals();
        }
    }

}
