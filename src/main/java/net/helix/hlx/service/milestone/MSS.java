package net.helix.hlx.service.milestone;

import net.helix.hlx.conf.HelixConfig;
import net.helix.hlx.service.API;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MSS {

    private static final Logger log = LoggerFactory.getLogger(MSS.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private HelixConfig config;
    private API api;

    private String address;
    private String message;
    private int delay;
    private int mwm;
    private Boolean sign;

    public MSS(HelixConfig configuration, API api) {
        this.config = configuration;
        this.api = api;
        this.delay = this.config.getMsDelay();
        int minDelay = this.config.getMinDelay();
        this.mwm = this.config.getMwm();
        this.message = StringUtils.repeat('0', 1024);
        this.address = "6a8413edc634e948e3446806afde11b17e0e188faf80a59a8b1147a0600cc5db";
        this.sign = !this.config.isDontValidateTestnetMilestoneSig();

        if(this.delay < minDelay) {
            this.delay = minDelay;
        }
    }

    private int getRound(long time) {
        return (int) (time - config.getGenesisTime()) / config.getRoundDuration();
    }

    private long getStartTime(int round) {
        return config.getGenesisTime() + (round * config.getRoundDuration());
    }

    public void startScheduledExecutorService() {
        log.info("MSS scheduledExecutorService started.");
        log.info("Submitting Milestones every: " + config.getRoundDuration() + "s.");
        int currentRound = getRound(System.currentTimeMillis());
        long startTimeNextRound = getStartTime(currentRound + 1);
        log.info("Next Round starts in " + ((startTimeNextRound - System.currentTimeMillis()) / 1000) + "s.");
        this.scheduledExecutorService.scheduleWithFixedDelay(this.getRunnablePublishMilestone(), (startTimeNextRound - System.currentTimeMillis()), config.getRoundDuration(),  TimeUnit.MILLISECONDS);
    }

    private void publishMilestone() throws Exception {
        log.info("Publishing next Milestone...");
        this.api.storeAndBroadcastMilestoneStatement(this.address, this.message, this.mwm, this.sign, 0);
        //this.api.storeAndBroadcastMilestoneStatement(this.address, this.message, this.mwm, this.sign, 2);
    }

    private Runnable getRunnablePublishMilestone() {
        return () -> {
            try {
                publishMilestone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void shutdown() {
        log.info("Shutting down MSS Thread");
        scheduledExecutorService.shutdown();
    }
}
