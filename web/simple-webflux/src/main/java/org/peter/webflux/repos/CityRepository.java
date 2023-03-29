package org.peter.webflux.repos;


import org.peter.webflux.models.City;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CityRepository {

    // may facing thread problem
    private final Map<Long, City> repository = new HashMap<>();

    {
        repository.put(1L, new City(1L));
        repository.put(2L, new City(2L));
        repository.put(3L, new City(3L));
        repository.put(4L, new City(4L));
        repository.put(5L, new City(5L));
        repository.put(6L, new City(6L));
    }

    private static final AtomicLong idGenerator = new AtomicLong(0);

    public Long save(City city) {
        Long id = idGenerator.incrementAndGet();
        city.setId(id);
        repository.put(id, city);
        return id;
    }

    public Collection<City> findAll() {

        System.out.println("findAllCity " + Thread.currentThread().getId() + " \\\\ " + this.hashCode());
        return repository.values();
    }


    public City findCityById(Long id) {
        return repository.get(id);
    }

    public Long updateCity(City city) {
        repository.put(city.getId(), city);
        return city.getId();
    }

    public Long deleteCity(Long id) {
        repository.remove(id);
        return id;
    }
}
