package RobotControl.cli;

import java.lang.reflect.Field;
import java.util.Objects;

public class CliOptions {
    public class Option<T> {
        private T value;

        public Option(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }
    public CliOptions(String args[]) throws IllegalAccessException {
        for(String arg : args) {
            String prefix = "";
            String specifier = "";
            String value = "";

            if(arg.startsWith("--")) {
                prefix = "--";
            } else if(arg.startsWith("-")) {
                prefix = "-";
            }

            if(prefix.equals("-")) {
                specifier = arg.substring(1, 2);
                value = arg.substring(2);
            } else if(prefix.equals("--")) {
                int valAssign = arg.indexOf('=');
                if (valAssign != -1) {
                    specifier = arg.substring(prefix.length(), valAssign);
                    value = arg.substring(valAssign);
                } else {
                    specifier = arg.substring(prefix.length());
                }
            }
            boolean isSimpleFlag = false;
            boolean isValid = false;
            for(Field field : this.getClass().getFields()) {
                Option<?> option = ((Option<?>) field.get(this));
                if(Objects.equals(field.getName(), specifier)) {
                    isValid = true;
                    if(field.getGenericType().getTypeName().contains("Option<java.lang.Boolean>")) {
                        isSimpleFlag = true;
                    }

                    if(isSimpleFlag) {
                        ((Option<Boolean>) option).value = true;
                    } else {
                        if(option.getValue() instanceof String) {
                            ((Option<String>) option).value = value;
                        } else if (option.getValue() instanceof Integer) {
                            ((Option<Integer>) option).value = Integer.parseInt(value);
                        }
                    }
                }
            }
            if(!isValid) {
                System.out.println("Option \"" + arg + "\" is not a valid option.");
                System.exit(1);
            }
        }
    }


    public Option<Integer> port = new Option<>(9000);
    public Option<Boolean> debug = new Option<>(false);
}
