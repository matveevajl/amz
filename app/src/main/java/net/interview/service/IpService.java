package net.interview.service;

import lombok.RequiredArgsConstructor;
import net.interview.model.IpEntity;
import net.interview.repository.IpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IpService {

    @Value("${ip.timeunit}")
    private String timeunit;

    @Value("${ip.timecount}")
    private int timecount;

    @Value("${ip.limit}")
    private long limit;

    private int calendarUnit;

    private final IpRepository ipRepository;

    @PostConstruct
    protected void init() {
        switch (TimeUnit.valueOf(timeunit)) {
            case MILLISECONDS:
                calendarUnit = Calendar.MILLISECOND;
                return;
            case SECONDS:
                calendarUnit = Calendar.SECOND;
                return;
            case MINUTES:
                calendarUnit = Calendar.MINUTE;
                return;
            default:
                throw new IllegalArgumentException(
                        "Use TimeUnit MILLISECONDS, SECONDS or MINUTES"
                );
        }
    }

    @Transactional
    public void save(String ip) {
        var ipEntity = new IpEntity();
        ipEntity.setIp(ip);
        ipRepository.save(ipEntity);
    }

    @Transactional(readOnly = true)
    public Boolean isFrequent(String ip) {
        var now = Calendar.getInstance();
        now.add(calendarUnit, -timecount);
        var count = ipRepository.countForIpForTimeRange(ip, now.getTime()).get(0).getIpCount();
        return count > limit;
    }

}
