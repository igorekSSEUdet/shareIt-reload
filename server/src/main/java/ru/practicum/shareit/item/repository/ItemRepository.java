package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long ownerId);

    Page<Item> findByOwner_Id(Long ownerId, Pageable pageable);

    List<Item> findByAvailableIsTrue();

    List<Item> findAllByRequestId(Long requestId);
}
