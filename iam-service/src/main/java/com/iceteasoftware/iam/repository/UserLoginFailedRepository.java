package com.iceteasoftware.iam.repository;


import com.iceteasoftware.iam.constant.GatewayCacheConstants;
import com.iceteasoftware.iam.entity.UserLoginFailed;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserLoginFailedRepository extends JpaRepository<UserLoginFailed, String> {

    @Cacheable(cacheNames = {GatewayCacheConstants.UserLoginFailed.FIND_BY_USER_ID}, key = "#userId",
            unless = "#result == null")
    default UserLoginFailed findByUserId(String userId) {
        return findById(userId).orElse(new UserLoginFailed(userId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Caching(put = {@CachePut(cacheNames = {GatewayCacheConstants.UserLoginFailed.FIND_BY_USER_ID},
            key = "#entity.userId")})
    default UserLoginFailed save_(UserLoginFailed entity) {
        return save(entity);
    }

}
