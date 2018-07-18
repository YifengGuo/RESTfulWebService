package hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by guoyifeng on 7/16/18.
 */
@RestController
public class DriverController {
    private final AtomicLong counter = new AtomicLong();

    private static Map<String, Driver> drivers = new HashMap<>();

    /**
     * add a new Driver into drivers by post method
     * @param
     * @return
     */
    @RequestMapping(value = "/drivers", method = RequestMethod.POST)
    public ResponseEntity<Driver> create(@RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName) {
        Driver driver = new Driver(firstName, lastName);
        long id = counter.incrementAndGet();
        driver.setId(id);
        drivers.put(String.valueOf(id), driver);
        return new ResponseEntity<>(driver, HttpStatus.CREATED);
    }

    /**
     * return all the recorded drivers
     * @return list of Driver
     */
    @RequestMapping(value = "/drivers", method = RequestMethod.GET)
    public ResponseEntity<List<Driver>> getAll() {
        return new ResponseEntity<>(new ArrayList<>(drivers.values()), HttpStatus.OK);
    }

    /**
     * get a certain driver by its id
     */
    @RequestMapping(value = "/drivers/{id}", method = RequestMethod.GET)
    public ResponseEntity<Driver> get(@PathVariable("id") String id) {
        Driver driver = null;
        if (!drivers.containsKey(id)) { // cannot find such Driver in the drivers
            return new ResponseEntity<Driver>(driver, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<Driver>(drivers.get(id), HttpStatus.OK);
        }
    }

    public static boolean isDriverValid(String id) {
        return drivers.containsKey(id);
    }

}
