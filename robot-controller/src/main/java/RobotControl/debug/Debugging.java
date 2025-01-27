package RobotControl.debug;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.ServerSocket;
import java.net.Socket;

public class Debugging {
    public static Map<String, String> getDebuggingInfo() {
        Map<String, String> debugInfo = new HashMap<>();

        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : jvmArgs) {
            if (arg.contains("-agentlib:jdwp")) {
                debugInfo.put("DebugAgent", "JDWP");
                String[] parts = arg.split(",");
                for (String part : parts) {
                    if (part.contains("=")) {
                        String[] keyValue = part.split("=", 2);
                        debugInfo.put(keyValue[0], keyValue[1]);
                    } else {
                        debugInfo.put(part, "true"); // Flags without values
                    }
                }
            }
        }

        return debugInfo;
    }

    public static void printDebuggingInfo() {
        Map<String, String> debugInfo = getDebuggingInfo();
        if (debugInfo.isEmpty()) {
            System.out.println("No debugging information detected.");
        } else {
            System.out.println("Debugging Information:");
            debugInfo.forEach((key, value) -> System.out.println(key + ": " + value));
        }
    }
}


