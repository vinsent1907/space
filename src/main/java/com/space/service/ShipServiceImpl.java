package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    public ShipServiceImpl() {

    }

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }


    /**
     * Возвращает корабль по его ID
     * @param ship передаем объект корабля который нужно сохранить
     * @return - объект сохраненного корабля
     */
    @Override
    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    /**
     * Возвращает корабль по его ID
     * @param id  ID корабля
     * @return - объект корабля с заданным ID
     */
    @Override
    public Ship getShip(Long id) {
        return shipRepository.findById(id).orElse(null);
    }


    /**
     * Удаляет корабль
     * @param ship передаем объект корабля который нужно удалить
     */
    @Override
    public void deleteShip(Ship ship) {
        shipRepository.delete(ship);
    }

    /**
     * Возвращает список кораблей с заданными необязательными параметрами:
     * @param   name  название корабля
     * @param   planet планета производства корабля
     * @param   shipType тип корабля
     * @param   after время производсва корабля
     * @param   before время производсвакорабля
     * @param   isUsed корабля
     * @param   minSpeed корабля
     * @param   maxSpeed корабля
     * @param   minCrewSize корабля
     * @param   maxCrewSize корабля
     * @param   minRating корабля
     * @param   maxRating корабля
     * @return - Возвращает List<Ship>
     */
    @Override
    public List<Ship> getShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    )
    {
         Date afterDate = after == null ? null : new Date(after);
         Date beforeDate = before == null ? null : new Date(before);
         List<Ship> list = new ArrayList<>();
        shipRepository.findAll().forEach((ship) -> {
            if (name != null && !ship.getName().contains(name)) return;
            if (planet != null && !ship.getPlanet().contains(planet)) return;
            if (shipType != null && ship.getShipType() != shipType) return;
            if (afterDate != null && ship.getProdDate().before(afterDate)) return;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) return;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) return;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) return;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) return;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) return;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) return;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) return;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) return;

            list.add(ship);
        });
        return list;
    }

    //Немного магии
    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID:
                        return ship1.getId().compareTo(ship2.getId());
                    case SPEED:
                        return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE:
                        return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING:
                        return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }
        return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
         Integer page = pageNumber == null ? 0 : pageNumber;
         Integer size = pageSize == null ? 3 : pageSize;
         int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @Override
    public boolean isValidShip(Ship ship) {
        return ship != null && isStringValid(ship.getName()) && isStringValid(ship.getPlanet())
                && isProdDateValid(ship.getProdDate())
                && isSpeedValid(ship.getSpeed())
                && isCrewSizeValid(ship.getCrewSize());
    }

    @Override
    public double calculateRating(double speed, boolean isUsed, Date prod) {
        final int now = 3019;
         int prodYear = getYearFromDate(prod);
         double k = isUsed ? 0.5 : 1;
        return round(80 * speed * k / (now - prodYear + 1));
    }

    @Override
    public Ship updateShip(Ship oldShip, Ship newShip) throws IllegalArgumentException {
        boolean shouldChangeRating = false;

         String name = newShip.getName();
        if (name != null) {
            if (isStringValid(name)) {
                oldShip.setName(name);
            } else {
                throw new IllegalArgumentException();
            }
        }
         String planet = newShip.getPlanet();
        if (planet != null) {
            if (isStringValid(planet)) {
                oldShip.setPlanet(planet);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (newShip.getShipType() != null) {
            oldShip.setShipType(newShip.getShipType());
        }
         Date prodDate = newShip.getProdDate();
        if (prodDate != null) {
            if (isProdDateValid(prodDate)) {
                oldShip.setProdDate(prodDate);
                shouldChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (newShip.getUsed() != null) {
            oldShip.setUsed(newShip.getUsed());
            shouldChangeRating = true;
        }
        Double speed = newShip.getSpeed();
        if (speed != null) {
            if (isSpeedValid(speed)) {
                oldShip.setSpeed(speed);
                shouldChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }
        Integer crewSize = newShip.getCrewSize();
        if (crewSize != null) {
            if (isCrewSizeValid(crewSize)) {
                oldShip.setCrewSize(crewSize);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (shouldChangeRating) {
            double rating = calculateRating(oldShip.getSpeed(), oldShip.getUsed(), oldShip.getProdDate());
            oldShip.setRating(rating);
        }
        shipRepository.save(oldShip);
        return oldShip;
    }

    private boolean isCrewSizeValid(Integer crewSize) {
        final int minCrewSize = 1;
        final int maxCrewSize = 9999;
        return crewSize != null && crewSize.compareTo(minCrewSize) >= 0 && crewSize.compareTo(maxCrewSize) <= 0;
    }

    private boolean isSpeedValid(Double speed) {
        final double minSpeed = 0.01;
        final double maxSpeed = 0.99;
        return speed != null && speed.compareTo(minSpeed) >= 0 && speed.compareTo(maxSpeed) <= 0;
    }

    private boolean isStringValid(String value) {
        final int maxStringLength = 50;
        return value != null && !value.isEmpty() && value.length() <= maxStringLength;
    }

    private boolean isProdDateValid(Date prodDate) {
        final Date startProd = getDateForYear(2800);
        final Date endProd = getDateForYear(3019);
        return prodDate != null && prodDate.after(startProd) && prodDate.before(endProd);
    }

    private Date getDateForYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    private int getYearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    //Округление до сотых
    private double round(double value) {
//        MathContext context = new MathContext(3, RoundingMode.HALF_UP);
//        BigDecimal result = new BigDecimal(value, context);
//        System.out.println((result.doubleValue()));
//        return result.doubleValue();
        double scale = Math.pow(10,2);
        return Math.round(value * scale) / scale;
    }

}
