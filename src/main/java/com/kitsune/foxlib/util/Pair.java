package com.kitsune.foxlib.util;

import java.util.Objects;

public class Pair<L, R> implements Cloneable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
