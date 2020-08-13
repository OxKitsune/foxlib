package com.kitsune.foxlib.util;

import java.util.Objects;

public class Triple<L, M, R> implements Cloneable {

    /**
     * The left value of the triple
     */
    private L left;

    /**
     * The middle value of the triple
     */
    private M middle;

    /**
     * The right value of the triple
     */
    private R right;

    /**
     * Construct a new {@link Triple <L, M, R>},.
     *
     * @param left - the left value of the triple
     * @param middle - the middle value of the triple
     * @param right - the right value of the triple
     */
    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    /**
     * Get the left value of the triple
     *
     * @return - the left value
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
     * Get the middle value of the triple.
     *
     * @return - the middle value of the triple
     */
    public M getMiddle() {
        return middle;
    }

    /**
     * Set the middle value of the triple
     *
     * @param middle - the middle value
     */
    public void setMiddle(M middle) {
        this.middle = middle;
    }

    /**
     * Get the right value of the triple
     *
     * @return - the right value
     */
    public R getRight() {
        return right;
    }

    /**
     * Set the right value of the triple
     *
     * @param right - the right value
     */
    public void setRight(R right) {
        this.right = right;
    }

    /**
     * Get a clone of this {@link Triple}.
     *
     * @return - a clone of this pair object
     */
    public Triple<L, M, R> clone () {
        return new Triple<L, M, R>(left, middle, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(left, triple.left) &&
                Objects.equals(middle, triple.middle) &&
                Objects.equals(right, triple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, middle, right);
    }
}
