import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 1000; i++) {
            executor.submit(new RouteAnalyzerTask());
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
            // Ожидаем
        }

        printResult();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void updateFrequency(int frequency) {
        synchronized (sizeToFreq) {
            sizeToFreq.put(frequency, sizeToFreq.getOrDefault(frequency, 0) + 1);
        }
    }

    public static void printResult() {
        int maxFrequency = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxFrequency = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        System.out.println("Самое частое количество повторений " + maxFrequency + ". Встретилось " + maxCount + " раз.");
        System.out.println("Другие размеры: ");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != maxFrequency) {
                System.out.println("- " + entry.getKey() + " " + entry.getValue() + " раз");
            }
        }
    }

    static class RouteAnalyzerTask implements Runnable {
        @Override
        public void run() {
            String route = generateRoute("RLRFR", 100);
            int countR = countChar(route, 'R');
            updateFrequency(countR);
            System.out.println("Маршрут: " + route + ". Количество 'R': " + countR);
        }

        private int countChar(String str, char c) {
            int count = 0;
            for (char ch : str.toCharArray()) {
                if (ch == c) {
                    count++;
                }
            }
            return count;
        }
    }
}
