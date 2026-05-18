package com.example.myShop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.myShop.entity.Member;
public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);

}
