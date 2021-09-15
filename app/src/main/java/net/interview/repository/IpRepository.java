package net.interview.repository;

import net.interview.model.IpCount;
import net.interview.model.IpEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IpRepository extends CrudRepository<IpEntity, Long> {

    @Query("select count(i.id) as ipCount from IpEntity as i where i.ip = ?1 and i.created > ?2")
    List<IpCount> countForIpForTimeRange(String ip, Date dateAfter);
}
