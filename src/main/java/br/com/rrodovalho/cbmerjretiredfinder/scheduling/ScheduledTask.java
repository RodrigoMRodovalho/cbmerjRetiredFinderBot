package br.com.rrodovalho.cbmerjretiredfinder.scheduling;
import br.com.rrodovalho.cbmerjretiredfinder.processor.Executor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by rrodovalho on 28/03/16.
 */
@Component
public class ScheduledTask {

    @Scheduled(cron = "0 12,21 * * 1-5 ?")//At 12:00 and 21:00 on Mon, Tue, Wed, Thu and Fri.
    public void executeRetiredSearch() {
        Executor.execute();
    }

}
