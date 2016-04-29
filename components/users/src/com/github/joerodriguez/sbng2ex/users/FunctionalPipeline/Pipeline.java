package com.github.joerodriguez.sbng2ex.users.FunctionalPipeline;

import java.util.Optional;

public class Pipeline {

    public static void functionalPipeline(){

    }

    public static class Either<L,R>{
        private final Optional<L> left;
        private final Optional<R> right;

        private Either(Optional<L> left, Optional<R> right) {
            this.left = left;
            this.right = right;
        }

        public static <L,R> Either left(L left){
            return new Either(Optional.of(left), Optional.<R>empty());
        }
        public static <L,R> Either Right(R right){
            return new Either(Optional.empty(), Optional.of(right));
        }

        public <T> Either<T, R> mapLeft(){
//            left.flatMap()
            return null;
        }
    }
}
