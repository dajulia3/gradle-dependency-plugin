package com.github.joerodriguez.sbng2ex.users;

public class ValidationErrorBoundToFields {



    public static WidgetFieldErrors validate(Widget widget){
//        return new WidgetFieldErrors();
    }


    public static class WidgetFieldErrors extends Widget{



        public WidgetFieldErrors(String name) {
            super(name);
        }
    }

    public static class FieldError{
        private String name;
        private String error;

        public FieldError(String name, String error) {
            this.name = name;
            this.error = error;
        }

        public String getName() {
            return name;
        }

        public String getError() {
            return error;
        }
    }
    public static class Widget{

        private final String name;

        public Widget(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
