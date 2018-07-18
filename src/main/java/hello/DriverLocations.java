package hello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyifeng on 7/17/18
 */

/**
 * Maintain a certain driver's locations info
 * each location has a unique locationId
 */
public class DriverLocations {
    private String driverId;

    // key: location id
    // value: location
    private Map<Long, Location> locations;

    private long currentLocationId;

    public DriverLocations(String driverId) {
        this.driverId = driverId;
        locations = new HashMap<>();
        currentLocationId = 0;
    }

    public void addLocation(Location location) {
        long id = ++currentLocationId;
        location.setId(id);
        locations.put(id, location);
    }

    /**
     * return all the locations for this driver
     * @return
     */
    public List<Location> getAll() {
        return new ArrayList<>(locations.values());
    }

    public Location getLastLocation() {
        return locations.get(currentLocationId);
    }

    public Location getLocation(long locationId) {
        return locations.containsKey(locationId) ? locations.get(locationId) : null;
    }

    public boolean updateLocation(long locationId, Location newLocation) {
        // cannot update location if it does not exist
        if (!locations.containsKey(locationId)) {
            return false;
        }
        Location location = locations.get(locationId);
        location.setLatitude(newLocation.getLatitude());
        location.setLongitude(newLocation.getLongitude());
        return true;
    }

    public boolean deleteLocation(long locationId) {
        if (!locations.containsKey(locationId)) {
            return false;
        }
        locations.remove(locationId);
        return true;
    }

}
