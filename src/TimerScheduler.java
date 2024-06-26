import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * La classe TimerScheduler serve per creare un oggetto DateTime, questo verrà incrementato di un tot di tempo specifico ogni
 */
public class TimerScheduler extends Thread {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private DateTime dateTime;
    private int incrementSeconds;
    private int incrementMinutes;
    private int incrementHours;
    private int incrementDays;
    private int reloadingMs = 1000;
    private final static int DEFAULT_RELOADING_MS = 1000;
    private boolean paused = false; // flag per gestire la pausa
    private boolean printing = true;

    /**
     * Costruttore per la classe TimerScheduler.
     *
     * @param dateTime L'oggetto DateTime da incrementare.
     * @param incrementSeconds I secondi da aggiungere ad ogni incremento.
     * @param incrementMinutes I minuti da aggiungere ad ogni incremento.
     * @param incrementHours Le ore da aggiungere ad ogni incremento.
     * @param incrementDays I giorni da aggiungere ad ogni incremento.
     */
    public TimerScheduler(DateTime dateTime, int incrementSeconds, int incrementMinutes, int incrementHours, int incrementDays) {
        this.dateTime = dateTime;
        this.incrementSeconds = incrementSeconds;
        this.incrementMinutes = incrementMinutes;
        this.incrementHours = incrementHours;
        this.incrementDays = incrementDays;
        this.reloadingMs = DEFAULT_RELOADING_MS;
    }
    public TimerScheduler(DateTime dateTime, int incrementSeconds, int incrementMinutes, int incrementHours, int incrementDays, int reloadingMs) {
        this.dateTime = dateTime;
        this.incrementSeconds = incrementSeconds;
        this.incrementMinutes = incrementMinutes;
        this.incrementHours = incrementHours;
        this.incrementDays = incrementDays;
        this.reloadingMs = reloadingMs;
    }

    /**
     * Avvia il timer che incrementa il DateTime e stampa il nuovo orario ad ogni incremento.
     *
     * @param intervalMillis L'intervallo di tempo in millisecondi tra ogni incremento.
     */
    public void start(long intervalMillis) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) { // controlla il flag paused
                dateTime.add(incrementSeconds, incrementMinutes, incrementHours, incrementDays);
                if(isPrinting())System.out.println(dateTime);
            }
        }, 0, intervalMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isPrinting(){
        return printing;
    }
    public void isPrinting(boolean printing){
        this.printing = printing;
    }
    /**
     * Ferma il timer.
     */
    public void stopTimer() {
        scheduler.shutdown();
    }

    /**
     * Mette in pausa il timer.
     */
    public boolean isPaused(){
        return this.paused;
    }
    public void isPaused(boolean paused){
        this.paused = paused;
    }

    /**
     * Riprende il timer se è in pausa.
     */
    public void resumeTimer() {
        paused = false;
    }

    /**
     * Avanza il timer di un numero specificato di intervalli di aggiornamento.
     * @param intervals Il numero di intervalli di aggiornamento da avanzare.
     */
    public void advanceByIntervals(int intervals) {
        // Calcola il totale del tempo da avanzare
        int totalSeconds = incrementSeconds * intervals;
        int totalMinutes = incrementMinutes * intervals;
        int totalHours = incrementHours * intervals;
        int totalDays = incrementDays * intervals;

        // Aggiungi il tempo calcolato al dateTime
        dateTime.add(totalSeconds, totalMinutes, totalHours, totalDays);
    }

    /**
     * default start
     */
    @Override
    public void run() {
        this.start(this.reloadingMs);
    }

    //setter-getter
    public synchronized DateTime getDateTime() {
        return dateTime;
    }
    public synchronized int getIncrementSeconds() {
        return incrementSeconds;
    }
    public synchronized int getIncrementMinutes() {
        return incrementMinutes;
    }
    public synchronized int getIncrementHours() {
        return incrementHours;
    }
    public synchronized int getIncrementDays() {
        return incrementDays;
    }
    public synchronized int getReloadingMs() {
        return reloadingMs;
    }
    public synchronized void setReloadingMs(int reloadingMs) {
        this.reloadingMs = reloadingMs;
    }

    /**
     * Metodo principale per testare la classe TimerScheduler.
     */
    public static void main(String[] args) {
        // Creiamo un oggetto DateTime con la data e l'ora corrente
        DateTime currentDateTime = new DateTime();

        // Creiamo un TimerScheduler che incrementa di 1 secondo ogni 1000 millisecondi (1 secondo)
        TimerScheduler timerScheduler = new TimerScheduler(currentDateTime, 0, 30, 0, 0, 1500);
        // Avviamo il timer
        timerScheduler.start();

        // Pausa e ripresa del timer
        try {
            Thread.sleep(5000); // Aspetta 5 secondi
            timerScheduler.isPaused(true);
            System.out.println("Timer in pausa...");

            // Avanza il timer di 3 intervalli di aggiornamento
            timerScheduler.advanceByIntervals(3);
            System.out.println("Timer avanzato di 3 intervalli...");

            Thread.sleep(5000); // Aspetta altri 5 secondi
            timerScheduler.resumeTimer();
            System.out.println("Timer ripreso...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
