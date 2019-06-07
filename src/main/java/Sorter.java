import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sorter {
    static List<Integer[]> arrays;
    static int step;

    public Integer[] sort(Integer[] a, int threads, Times times) {
        return parallelMergeSort(a, threads, times);
        //return mergeSort2(a);
    }

    public Integer[] sortOMP(Integer[] a, int threads, Times times) {
        return parallelMergeSortOMP(a, threads, times);
    }

    public Integer[] parallelMergeSort(Integer[] a, int availableThreads, Times times) {

        step = a.length / availableThreads;
        List<ThreadExtended> threads = new ArrayList<>();

        arrays = new ArrayList<>();

        int from = 0;
        int to = step;

        if (availableThreads > 1) {
            for (int i = 0; i < availableThreads; i++) {
                Integer[] e = Arrays.copyOfRange(a, from, to);

                from += step;
                to += step;

                ThreadExtended part = new ThreadExtended(e);
                threads.add(part);
            }

            long startTime = System.currentTimeMillis();
            for (ThreadExtended t : threads) {
                t.start();
            }
            try {
                for (ThreadExtended t : threads) {
                    t.join();
                    arrays.add(t.arr);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();

            times.setTt(endTime - startTime);
            Integer[] res = new Integer[a.length];

            startTime = System.currentTimeMillis();
            res = recMerger(arrays);
            endTime = System.currentTimeMillis();
            times.setTm(endTime - startTime);


            return res;
        } else {
            return mergeSort2(a);
        }

    }

    public Integer[] parallelMergeSortOMP(Integer[] a, int availableThreads, Times times) {

        int step = a.length / availableThreads;
        List<ThreadExtended> threads = new ArrayList<>();

        arrays = new ArrayList<>();
        if (availableThreads > 1) {
            long startTime = System.currentTimeMillis();

            int from = 0;
            int to = step;

            // omp parallel for schedule(static)
            for (int i = 0; i < availableThreads; i++) {
                Integer[] e = Arrays.copyOfRange(a, from, to);
                from += step;
                to += step;
                e = mergeSort2(e);
                arrays.add(e);
            }
            long endTime = System.currentTimeMillis();
            times.setTt(endTime - startTime);
           /* System.out.println("sorted arrays:");
            for (Integer[] k : arrays) {
                System.out.print(k.length + " |");
                for (Integer n : k) System.out.printf("%7d", n);
                System.out.println();
            }*/
            // System.out.println("merging process:");
            System.out.print("");
            startTime = System.currentTimeMillis();
            Integer[] res = recMergerOMP(arrays);
            endTime = System.currentTimeMillis();

            times.setTm(endTime - startTime);
           /* System.out.println("result array:");
            for (int i = 0; i < res.length; i++)
            {
                System.out.printf("%7d",res[i]);
                if(i>0&&i%step==0)
                    System.out.println();
            }*/
            return res;

        } else {
            return mergeSort2(a);
        }

    }


    public static Integer[] mergeSort2(Integer[] a) {
        if (a.length < 2) {
            return a;
        } else {
            int mid = a.length / 2;
            Integer[] left;
            Integer[] right;
            if (a.length > 2) {
                left = Arrays.copyOfRange(a, 0, mid);
                right = Arrays.copyOfRange(a, mid, a.length);
                left = mergeSort2(left);
                right = mergeSort2(right);
            } else {
                left = new Integer[1];
                left[0] = a[0];

                right = new Integer[1];
                right[0] = a[1];
            }

            Integer[] res = merge2(left, right);
            return res;
        }
    }

    public static Integer[] merge2(Integer[] l, Integer[] r) {
        int n = l.length + r.length;

        Integer[] res = new Integer[n];

        int i1 = 0;
        int i2 = 0;
        int j = 0;

        while (i1 < l.length && i2 < r.length) {
            if (l[i1] < r[i2]) {
                res[j] = l[i1];
                i1++;
            } else {
                res[j] = r[i2];
                i2++;
            }
            j++;
        }

        while (i1 < l.length) {
            res[j] = l[i1];
            i1++;
            j++;
        }

        while (i2 < r.length) {
            res[j] = r[i2];
            i2++;
            j++;
        }
        return res;
    }

    public Integer[] recMerger(List<Integer[]> a) {
        if (a.size() == 1)
            return a.get(0);

        List<ThreadMerger> threads = new ArrayList<>();
        List<Integer[]> res = new ArrayList<>();
        for (int i = 0; i < a.size() - 1; i += 2) {
            ThreadMerger part = new ThreadMerger(a.get(i), a.get(i + 1));
            threads.add(part);
        }
        for (ThreadMerger t : threads) {
            t.start();
        }
        try {
            for (ThreadMerger t : threads) {
                t.join();
                res.add(t.result);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return recMerger(res);
    }

    public Integer[] recMergerOMP(List<Integer[]> a) {
        if (a.size() == 1)
            return a.get(0);

        List<Integer[]> res = new ArrayList<>();

        // omp parallel for schedule(static)
        for (int i = 0; i < a.size() - 1; i += 2) {
            System.out.print("");
            //   System.out.printf("%7d:%7d\n",a.get(i).length,a.get(i).length);
            res.add(merge2(a.get(i), a.get(i + 1)));
        }
        return recMergerOMP(res);
    }

    public class ThreadExtended extends Thread {
        Integer[] arr;
        long systime;

        public ThreadExtended(Integer[] arr) {
            this.arr = arr;
        }

        @Override
        public void run() {
            super.run();
            long startTime = System.currentTimeMillis();
            arr = mergeSort2(arr);
            long endTime = System.currentTimeMillis();
            systime = endTime - startTime;

        }
    }

    public class ThreadMerger extends Thread {
        Integer[] result;
        Integer[] l;
        Integer[] r;

        public ThreadMerger(Integer[] l, Integer[] r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public void run() {
            super.run();
            result = merge2(l, r);
        }
    }
}
