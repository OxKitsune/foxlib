package com.kitsune.foxlib.util;

public class Pair<L, R> {

    /**
     * The left value of the pair
     */
    private L left;

    /**
     * The right value of the pair
     */
    private R right;

    /**
     * Construct a new {@link Pair<L, R>},.
     *
     * @param left - the left value of the pair
     * @param right - the right value of the pair
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Get the left value of the pair
     *
     * @return - the left value of the pair
     */
    public L getLeft() {
        return left;
    }

    /**
     * Set the left value of the pair.
     *
     * @param left - the left value
     */
    public void setLeft(L left) {
        this.left = left;
    }

    /**
     * Get the right value of the pair
     *
     * @return - the right value
     */
    public R getRight() {
        return right;
    }

    /**
     * Set the right value of the pair
     *
     * @param right - the right value
     */
    public void setRight(R right) {
        this.right = right;
    }

    /**
     * Get a clone of this {@link Pair}.
     *
     * @return - a clone of this pair object
     */
    public Pair<L, R> clone () {
        return new Pair<L, R>(left, right);
    }
}
