package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByFcmToken(String fcmToken);

    boolean existsByEmail(String email);
}
