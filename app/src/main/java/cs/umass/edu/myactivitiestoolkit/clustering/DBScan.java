package cs.umass.edu.myactivitiestoolkit.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snoran on 11/1/15.
 * <p>
 * DBScan clustering implementation. Given a set of points, the DBScan algorithm
 * assigns each point to a cluster such that for any point P in cluster C, each
 * neighbors P' of P is added to C if P has at least {@link #minPts} neighbors.
 * A point Q is considered a neighbor of P if {@code distance(P,Q)} is at most
 * {@link #eps}. Additionally, points may be marked as noise, meaning they fall
 * in no cluster.
 *
 * @author CS390MB
 * @see Clusterable
 * @see Cluster
 */
public class DBScan<T extends Clusterable<T>> {

  /**
   * Radius of the neighborhood for expanding clusters
   */
  private final double eps;

  /**
   * Minimum number of points in a cluster.
   */
  private final int minPts;

  /**
   * @param eps    radius of the neighborhood for expanding clusters
   * @param minPts minimum number of points in a cluster
   */
  public DBScan(final double eps, final int minPts) {
    this.eps = eps;
    this.minPts = minPts;
  }

  /**
   * The state of a point - is it in a cluster or has it been marked as noise?
   */
  private enum State {
    /**
     * The point has not yet been visited
     */
    UNVISITED, /**
     * The point has been visited and has determined to be noise (no cluster)
     */
    NOISE, /**
     * The point has been assigned to a cluster.
     */
    CLUSTERED
  }

  /**
   * Returns the radius of the neighborhood for expanding clusters
   *
   * @return epsilon
   */
  public double getEps() {
    return eps;
  }

  /**
   * Returns the minimum number of points in a cluster
   *
   * @return minPts
   */
  public int getMinPts() {
    return minPts;
  }

  /**
   * DBScan clustering algorithm. See the slides for pseudocode and the class
   * documentation for a detailed description.
   * <p>
   * This method will return a list of clusters. Each {@link Cluster} contains
   * a list of points that belong to that cluster. DBScan does not return
   * cluster centers. To find the center, simply average over the points.
   * </p>
   * <p>
   * Points must be valid for clustering - that is a subclass of
   * {@link Clusterable}. This is so that we can compute the distance as defined
   * by {@link Clusterable#distance(Object)}.
   * </p>
   *
   * @param points the list of points we want to cluster
   * @return a list of clusters
   * @see Cluster
   * @see Clusterable
   * @see #eps
   * @see #minPts
   */
  public List<Cluster<T>> cluster(final Collection<T> points) {
    // The list of clusters - the algorithm will populate these
    List<Cluster<T>> clusters = new ArrayList<>();

    // Maps a point T to true or false, indicating whether that point has been
    // visited or not
    Map<T, State> states = new HashMap<>();

    //initialize all points' state to UNVISITED
    for (final T p : points) {
      states.put(p, State.UNVISITED);
    }

    //TODO: Implement the DBScan algorithm - currently the code returns a single
    // cluster containing all points
    Cluster<T> cluster = null;
    for (final T p : points) {
      // Check if the point has been visited
      if (states.get(p) == State.UNVISITED) {
        // Get the neighbors of the current point
        List<T> neighbors = regionQuery(p, points);

        // Expand the cluster
        if (neighbors.size() >= getMinPts()) {
          cluster = new Cluster<>();
          clusters.add(cluster);
          expandCluster(cluster, p, states, neighbors, points);
        }
        // The point has been visited, but it and its neighbors don't satisfy
        // the minimum number of points in a cluster, so it's noise
        else {
          states.put(p, State.NOISE);
        }
      }
    }

    return clusters;
  }

  /**
   * Expands the cluster to include reachable points within {@link #eps}
   * satisfying a local density defined by {@link #minPts}.
   *
   * @param cluster     the cluster to expand
   * @param p           Point to add to cluster
   * @param states      a map from points to true or false,
   *                    indicating whether the point has been visited
   * @param neighborPts the list of neighboring points
   * @param points      the set of all points
   */
  private void expandCluster(
    Cluster<T> cluster,
    final T p,
    final Map<T, State> states,
    final List<T> neighborPts,
    final Collection<T> points
  ) {
    // we added the point p to the cluster and also flagged it as clustered for
    // you, to demonstrate how to do that
    cluster.addPoint(p);
    states.put(p, State.CLUSTERED);

    // TODO: Complete the rest of the expandCluster algorithm, as outlined in
    // the slides
    for (int i = 0; i < neighborPts.size(); i++) {
      T currPoint = neighborPts.get(i);

      // Only take account of unvisited points
      if (states.get(currPoint) == State.UNVISITED) {
        states.put(currPoint, State.CLUSTERED);
        cluster.addPoint(currPoint);
        List<T> neighbors = regionQuery(currPoint, points);

        // Add any neighbors that aren't already in neighborPts and mark the
        // current point as clustered
        if (neighbors.size() >= getMinPts()) {
          addAsSet(neighborPts, neighbors);
        }
      }

      if (states.get(currPoint) != State.CLUSTERED) {
        cluster.addPoint(currPoint);
      }
    }
  }

  /**
   * Returns a list of neighbors within {@link #eps} of the given point.
   *
   * @param p      the point of interest
   * @param points all candidate neighboring points
   * @return a list of neighbors
   * @see Clusterable#distance(Object)
   */
  private List<T> regionQuery(final T p, final Collection<T> points) {
    //TODO: Query the region around point p to get its neighbors, that is all
    // points within eps of p
    final List<T> neighbors = new ArrayList<>();

    // Add every point to neighbors that falls within eps
    for (final T pt : points) {
      if (p.distance(pt) <= getEps()) {
        neighbors.add(pt);
      }
    }

    return neighbors;
  }

  /**
   * Adds all the items in list2 to list1 that are not already contained in
   * list1. It's important that we don't have repeats in our clusters
   *
   * @param list1 the first list of points
   * @param list2 the second list of points
   */
  private void addAsSet(List<T> list1, final List<T> list2) {
    for (T p : list2) {
      if (!list1.contains(p)) {
        list1.add(p);
      }
    }
  }
}
