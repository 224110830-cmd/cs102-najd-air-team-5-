package com.mycompany.cs102.project.step1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A request to move an economy booking to business class.
 * One flight keeps its requests in a PriorityQueue, and compareTo() below
 * decides who is at the front of the queue.
 *
 * Waitlist order:
 *   1. higher tier first  (PLATINUM, GOLD, SILVER, NONE)
 *   2. same tier: the earlier request goes first
 *
 * The tier is stored when the request is made (tierAtRequest). If the
 * passenger's tier changes later, a request that is already waiting does
 * NOT move.
 *
 * @author Member 2
 */
public class UpgradeRequest implements Storeable, Comparable<UpgradeRequest> {

    private static final DateTimeFormatter FILE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String bookingRef;
    private final FrequentFlyerTier tierAtRequest;
    private final LocalDateTime requestedAt;

    /** Used when a passenger asks for an upgrade right now (menu option 11). */
    public UpgradeRequest(String bookingRef, FrequentFlyerTier tierAtRequest,
                          LocalDateTime requestedAt) throws InvalidDataException {
        if (bookingRef == null || !bookingRef.matches("^BK\\d{4,}$")) {
            throw new InvalidDataException("Invalid booking reference in upgrade request.");
        }
        if (tierAtRequest == null) {
            throw new InvalidDataException("Upgrade request needs a tier.");
        }
        if (requestedAt == null) {
            throw new InvalidDataException("Upgrade request needs a time.");
        }
        this.bookingRef = bookingRef;
        this.tierAtRequest = tierAtRequest;
        this.requestedAt = requestedAt;
    }

    /**
     * Used by FileManager.loadUpgrades(), where the time is still text,
     * for example "2026-10-05 14:15:00".
     * Text that is not a real date throws, and the loader skips that line.
     */
    public UpgradeRequest(String bookingRef, FrequentFlyerTier tierAtRequest,
                          String requestedAt) throws InvalidDataException {
        this(bookingRef, tierAtRequest, parseTime(requestedAt));
    }

    private static LocalDateTime parseTime(String text) throws InvalidDataException {
        try {
            // "2026-10-05 14:15:00" -> "2026-10-05T14:15:00" (the format Java reads)
            return LocalDateTime.parse(text.trim().replace(" ", "T"));
        } catch (Exception e) {
            throw new InvalidDataException("Invalid request time: " + text);
        }
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public FrequentFlyerTier getTierAtRequest() {
        return tierAtRequest;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    // ---------- the waitlist order ----------

    @Override
    public int compareTo(UpgradeRequest other) {
        // In FrequentFlyerTier, PLATINUM has priority 1 and NONE has 4,
        // so the SMALLER number must come first.
        int byTier = Integer.compare(this.tierAtRequest.getPriority(),
                                     other.tierAtRequest.getPriority());
        if (byTier != 0) {
            return byTier;
        }
        // same tier: earlier request first
        int byTime = this.requestedAt.compareTo(other.requestedAt);
        if (byTime != 0) {
            return byTime;
        }
        // completely tied: use the reference so the order is always the same
        return this.bookingRef.compareTo(other.bookingRef);
    }

    // ---------- Storeable ----------

    /** The booking reference identifies the request (one request per booking). */
    @Override
    public String getId() {
        return bookingRef;
    }

    /** bookingRef,tierAtRequest,requestedAt  (same order as upgrades.csv) */
    @Override
    public String toFileLine() {
        return bookingRef + "," + tierAtRequest + "," + requestedAt.format(FILE_FORMAT);
    }

    @Override
    public String toString() {
        return bookingRef + " | " + tierAtRequest + " | requested " + requestedAt.format(FILE_FORMAT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpgradeRequest other = (UpgradeRequest) o;
        return Objects.equals(this.bookingRef, other.bookingRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingRef);
    }
}
