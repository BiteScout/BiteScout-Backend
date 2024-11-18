package com.bitescout.app.userservice.repository;
import com.bitescout.app.userservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<User, UUID> {

}
