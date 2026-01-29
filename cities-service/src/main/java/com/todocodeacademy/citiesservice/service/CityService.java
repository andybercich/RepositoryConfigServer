package com.todocodeacademy.citiesservice.service;

import com.todocodeacademy.citiesservice.dto.CityDTO;
import com.todocodeacademy.citiesservice.model.City;
import com.todocodeacademy.citiesservice.repository.IHotelsAPI;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityService implements ICityService{

    @Autowired
   private IHotelsAPI hotelsAPI;

    List<City> cities = new ArrayList<City>();

    public CityDTO fallBacksGetsCitiesHotel(Throwable throwable){
        return new CityDTO(9999999L, "Fallido", "Fallido", "Fallido", "Fallido", null);
    }

    //Circuit nombre del servicio donde ocurre el corto circuito
    //Fall back method redirige la consulta a otro metodo que le digamos
    //Retry hace lo que vos le decis despues de una excepcion
    @Override
    @CircuitBreaker(name = "hotels-service", fallbackMethod = "fallBacksGetsCitiesHotel")
    @Retry(name = "hotels-service", fallbackMethod = "retryMethod")
    public CityDTO getCitiesHotels(String name, String country) {

        //buscamos ciudad original
        City city = this.findCity(name, country);

        //creamos el DTO de la ciudad + lista de hoteles
        CityDTO cityDTO = new CityDTO();
        cityDTO.setCity_id(city.getCity_id());
        cityDTO.setName(city.getName());
        cityDTO.setCountry(city.getCountry());
        cityDTO.setContinent(city.getContinent());
        cityDTO.setState(city.getState());

        //buscamos la lista de hoteles en la API y asignamos


        cityDTO.setHotelList(hotelsAPI.getHotelsByCityId(city.getCity_id()));


        return cityDTO;
    }

    public void createException() {
        throw new IllegalArgumentException("´Prueba de resiliance");
    }
    public CityDTO retryMethod(Throwable t) {
        System.out.println("Retry agotado motivo: " + t.getMessage());

        // Devuelve un CityDTO seguro o vacío
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Retry fallo");
        cityDTO.setCountry("Retry fallo");
        cityDTO.setHotelList(new ArrayList<>());
        return cityDTO;
    }

    public City findCity(String name, String country) {
        this.loadCities();
        for (City c:cities) {
            if (c.getName().equals(name)) {
                if (c.getCountry().equals(country)) {
                    return c;
                }

            }

        }
        return null;
    }

    public void loadCities () {

        cities.add(new City(1L, "Buenos Aires", "South America", "Argentina", "Buenos Aires"));
        cities.add(new City(2L, "Oberá", "South America", "Argentina", "Misiones"));
        cities.add(new City(3L, "Mexico City", "North America", "Mexico", "Mexico City"));
        cities.add(new City(4L, "Guadalajara", "North America", "Mexico", "Jalisco"));
        cities.add(new City(5L, "Bogotá", "South America", "Colombia", "Cundinamarca"));
        cities.add(new City(6L, "Medellín", "South America", "Colombia", "Antioquia"));
        cities.add(new City(7L, "Santiago", "South America", "Chile", "Santiago Metropolitan"));
        cities.add(new City(8L, "Valparaíso", "South America", "Chile", "Valparaíso"));
        cities.add(new City(9L, "Asunción", "South America", "Paraguay", "Asunción"));
        cities.add(new City(10L, "Montevideo", "South America", "Uruguay", "Montevideo"));
        cities.add(new City(11L, "Madrid", "Europe", "Spain", "Community of Madrid"));
        cities.add(new City(12L, "Barcelona", "Europe", "Spain", "Catalonia"));
        cities.add(new City(13L, "Seville", "Europe", "Spain", "Andalucia"));
        cities.add(new City(14L, "Monterrey", "North America", "Mexico", "Nuevo León"));
        cities.add(new City(15L, "Valencia", "Europe", "Spain", "Valencian Community"));

    }
}
