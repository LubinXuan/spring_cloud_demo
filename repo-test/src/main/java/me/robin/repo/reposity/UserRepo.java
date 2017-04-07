package me.robin.repo.reposity;

import me.robin.repo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@RepositoryRestResource(path = "user")
public interface UserRepo extends JpaRepository<User, Integer> {
}
