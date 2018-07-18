package hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by guoyifeng on 7/16/18.
 */
@RestController
public class LocationServiceController {
    private final AtomicLong counter = new AtomicLong();
    private Random random = new Random();

    // key: driverId
    // value: list of locations of this driver
    private Map<String, DriverLocations> locationsMap = new HashMap<>();

    /**
     * post a location for a given driver
     * @param id Driver Id
     * @param inputLocation input location
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations", method = RequestMethod.POST)
    public ResponseEntity<Location> create(
            @PathVariable("id") String id,
            @RequestBody(required = false) Location inputLocation
    ) {
        Location location;
        if (inputLocation == null) {
            location = new Location(random.nextInt(90), random.nextInt(90));
        } else {
            location = new Location(inputLocation.getLatitude(), inputLocation.getLongitude());
        }
        // if current driver is not in the table, put it first
        if (!locationsMap.containsKey(id)) {
            locationsMap.put(id, new DriverLocations(id));
        }
        locationsMap.get(id).addLocation(location);
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }

    /**
     * get all the locations for the given driver
     * @param id Driver Id
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations", method = RequestMethod.GET)
    public ResponseEntity<List<Location>> getAll(@PathVariable("id") String id) {
        // sanity check
        if (!locationsMap.containsKey(id)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        DriverLocations driverLocations = locationsMap.get(id);

        // if current driver has no locations cached
        if (driverLocations == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(driverLocations.getAll(), HttpStatus.OK);
        }
    }

    /**
     * get the specific location of a given driver at given locationId
     * @param id
     * @param locationId
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations/{locationId}", method = RequestMethod.GET)
    public ResponseEntity<Location> get(@PathVariable("id") String id,
                                        @PathVariable("locationId") String locationId) {
        Location location = null; // returned location
        // sanity check
        if (!locationsMap.containsKey(id)) {
            return new ResponseEntity<>(location, HttpStatus.BAD_REQUEST);
        }

        DriverLocations driverLocations = locationsMap.get(id);

        location = driverLocations.getLocation(Long.parseLong(locationId));
        // if given locationId is not stored in the driverLocations
        if (location == null) {
            return new ResponseEntity<>(location, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(location, HttpStatus.OK);
        }
    }

    /**
     * get the current (or latest) location of given driver
     * @param id
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations/current", method = RequestMethod.GET)
    public ResponseEntity<Location> getCurrent(@PathVariable("id") String id) {
        Location location = null; // returned location
        // sanity check
        if (!locationsMap.containsKey(id)) {
            return new ResponseEntity<>(location, HttpStatus.BAD_REQUEST);
        }
        DriverLocations driverLocations = locationsMap.get(id);
        location = driverLocations.getLastLocation();

        if (location == null) {
            return new ResponseEntity<>(location, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(location, HttpStatus.OK);
        }
    }

    /**
     * update a certain location for given driver at given locationId
     * @param id Driver Id
     * @param locationId
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations/{locationId}", method = RequestMethod.PUT)
    public ResponseEntity<Location> update(@PathVariable("id") String id,
                                           @PathVariable("locationId") String locationId,
                                           @RequestBody(required = false) Location newLocation) {
        Location temp = null;
        // sanity check
        if (!locationsMap.containsKey(id)) {
            return new ResponseEntity<>(temp, HttpStatus.BAD_REQUEST);
        }

        DriverLocations driverLocations = locationsMap.get(id);

        if (driverLocations.updateLocation(Long.parseLong(locationId), newLocation)) {
            return new ResponseEntity<>(newLocation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(temp, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * delete a driver's location given that locationId
     * @param id
     * @param locationId
     * @return
     */
    @RequestMapping(value = "/drivers/{id}/locations/{locationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Location> delete(@PathVariable("id") String id,
                                           @PathVariable("locationId") String locationId) {
        return this.deleteIml(id, locationId);
    }


    @RequestMapping(value = "/drivers/{id}/locations/{locationId}/delete", method = RequestMethod.POST)
    public ResponseEntity<Location> deleteByPost(@PathVariable("id") String id,
                                                 @PathVariable("locationId") String locationId) {
        return this.deleteIml(id, locationId);
    }



    /**
     * real implementation for deletion on driver's location based on given locationId
     * this method will be used both by DELETE and POST
     */
    private ResponseEntity<Location> deleteIml(@PathVariable("id") String id,
                                               @PathVariable("locationId") String locationId) {
        Location deletedLocation = null;
        // sanity check
        if (!locationsMap.containsKey(id)) {
            return new ResponseEntity<>(deletedLocation, HttpStatus.BAD_REQUEST);
        }

        DriverLocations driverLocations = locationsMap.get(id);

        deletedLocation = driverLocations.getLocation(Long.parseLong(locationId));

        if (driverLocations.deleteLocation(Long.parseLong(locationId))) {
            return new ResponseEntity<>(deletedLocation, HttpStatus.NO_CONTENT); // for deletion usage
        } else {
            return new ResponseEntity<>(deletedLocation, HttpStatus.BAD_REQUEST);
        }
    }


}
