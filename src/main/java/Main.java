import java.util.Comparator;
import java.util.Random;

public class Main {
    static int REPEATS = 100,
            SIZE_FIXED = 1000000,
            availableThreads = (Runtime.getRuntime().availableProcessors() * 4);

    public static void main(String[] args) {
        testNCores();
    }

    public static void testNCores() {

        Comparator<Integer> comp = new Comparator<Integer>() {
            @Override
            public int compare(Integer d1, Integer d2) {
                return d1.compareTo(d2);
            }
        };

        System.out.printf("\nMax number of threads == %d\n\n", availableThreads);
        // b = createRandomArray(SIZE_FIXED);

        for (int i = 1; i <= availableThreads; i *= 2) {

            if (i == 1) {
                System.out.printf("%d Thread:\n", i);
            } else {
                System.out.printf("%d Threads:\n", i);
            }
            long[] res = new long[REPEATS];
            long[] resm = new long[REPEATS];
            long[] resOMP = new long[REPEATS];
            System.out.printf("%d Repeats, %d elements:\n\n", REPEATS, SIZE_FIXED);
            for (int j = 1; j <= REPEATS; j++) {

                Integer[] a = createRandomArray(SIZE_FIXED);
                //   Integer[] b = Arrays.copyOf(a, SIZE_FIXED);
                Integer[] b;

                Times times = new Times();
                long startTime = System.currentTimeMillis();
                b = (new Sorter()).sortOMP(a, i, times);
                long endTime = System.currentTimeMillis();

     /*           if (!isSorted(b, a.length)) {
                    System.out.println("Default not sorted afterward!");
                    throw new RuntimeException("Sort error");
                }*/
                if (i == 1)
                    res[j - 1] = endTime - startTime;
                else {
                    res[j - 1] = times.getTt();
                    resm[j - 1] = times.getTm();
                }

             /*   startTime = System.currentTimeMillis();
                MergeSorter.sortOMP(b, comp, i);
                endTime = System.currentTimeMillis();*/

        /*        if (!isSorted(b, comp)) {
                    throw new RuntimeException("Omp not sorted afterward!");
                }*/
                //     resOMP[j - 1] = endTime - startTime;
            }
            if (i == 1) {
                System.out.println("    Default:");
                calculateResults(res);
            } else {
                System.out.println("    Threads:");
                calculateResults(res);
                System.out.println("    Merge:");
                calculateResults(resm);
            }
       /*     System.out.println("    OMP:");
            calculateResults(resOMP);*/
        }
    }

    public static <E> boolean isSorted(E[] a, Comparator<E> comp) {
        for (int i = 0; i < a.length - 1; i++) {
            if (comp.compare(a[i], a[i + 1]) > 0) {
                System.out.println(a[i] + " > " + a[i + 1]);
                return false;
            }
        }
        return true;
    }

    public static boolean isSorted(Integer[] a, int size) {
        if (a.length != size) {
            System.out.println("Size doesnt match " + size + " != " + a.length);
            return false;
        }
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i] > a[i + 1]) {
                System.out.println(a[i] + " > " + a[i + 1]);
                return false;
            }
        }
        return true;
    }

    public static Integer[] createRandomArray(int length) {
        Integer[] a = new Integer[length];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < a.length; i++) {
            a[i] = rand.nextInt(1000000);
        }
        return a;
    }

    public static void calculateResults(long[] res) {

        //P=0.95, n=100
        double student = 1.9840;

        //Среднее значение
        long x = 0;
        long x_s = 0;
        for (int j = 0; j < REPEATS; j++) {
            x += res[j];
            x_s += Math.pow(res[j], 2);
        }

        double x_square = x_s * 1.0 / REPEATS;
        double x_middle = x * 1.0 / REPEATS;

        double S = 0;

        //СКО
        for (int j = 0; j < REPEATS; j++) {
            S += Math.pow(res[j] - x_middle, 2);
        }
        S = S / (REPEATS);
        S = Math.sqrt(S);

        //Погрешность
        double z = student * S / Math.sqrt(REPEATS);

        //Дисперсия
        double D = x_square - Math.pow(x_middle, 2);
 /*       System.out.printf("M[X] = %10f\n" +
                "M[X^2] = %10f\n" +
                "M[X]^2 = %10f\n" +
                "x = %10f +- %10f, S = %10f, D = %10f\n", x_middle, x_square, Math.pow(x_middle, 2), x_middle, z, S, D);*/
        System.out.printf("    x = %10f +- %8f, S = %10f, D = %10f\n", x_middle, z, S, D);
        System.out.print("\n");
    }
}
