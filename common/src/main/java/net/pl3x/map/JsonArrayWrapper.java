package net.pl3x.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import net.pl3x.map.markers.option.Option;

/**
 * Represents a wrapped {@link JsonArray} with extra '<code>add</code>' methods,
 * because Google made it a final class.
 */
@SuppressWarnings("unused")
public class JsonArrayWrapper {
    public static final JsonArray NULL_ARRAY = new JsonArray();

    private final JsonArray wrapped;

    /**
     * Creates an empty JsonArrayWrapper.
     */
    public JsonArrayWrapper() {
        this.wrapped = new JsonArray();
    }

    /**
     * Creates an empty JsonArrayWrapper.
     */
    public JsonArrayWrapper(int capacity) {
        this.wrapped = new JsonArray(capacity);
    }

    /**
     * Get the underlying JsonArray object.
     *
     * @return JsonArray
     */
    public JsonArray getJsonArray() {
        return this.wrapped;
    }

    // Let's add some stuff..

    /**
     * Adds the specified {@link Boolean} to self.
     *
     * @param bool the boolean that needs to be added to the array.
     */
    public void add(Boolean bool) {
        getJsonArray().add(bool == null ? null : bool ? 1 : 0);
    }

    /**
     * Adds the specified {@link Enum} to self.
     *
     * @param enumeration the enum that needs to be added to the array.
     */
    public void add(Enum<?> enumeration) {
        getJsonArray().add(enumeration == null ? null : enumeration.ordinal());
    }

    /**
     * Adds the specified {@link Key} to self.
     *
     * @param key the enum key needs to be added to the array.
     */
    public void add(Key key) {
        getJsonArray().add(key == null ? null : key.toString());
    }

    /**
     * Adds the specified {@link JsonSerializable} to self.
     *
     * @param serializable the serializable that needs to be added to the array.
     */
    public void add(JsonSerializable serializable) {
        getJsonArray().add(serializable == null ? null : serializable.toJson());
    }

    /**
     * Adds the specified {@link Option} to self.
     *
     * @param option the option that needs to be added to the array.
     */
    public void add(Option<?> option) {
        getJsonArray().add(option == null ? NULL_ARRAY : option.toJson());
    }

    // Now let's finish with the existing stuff from JsonArray

    /**
     * Adds the specified character to self.
     *
     * @param character the character that needs to be added to the array.
     */
    public void add(Character character) {
        getJsonArray().add(character);
    }

    /**
     * Adds the specified number to self.
     *
     * @param number the number that needs to be added to the array.
     */
    public void add(Number number) {
        getJsonArray().add(number);
    }

    /**
     * Adds the specified string to self.
     *
     * @param string the string that needs to be added to the array.
     */
    public void add(String string) {
        getJsonArray().add(string);
    }

    /**
     * Adds the specified element to self.
     *
     * @param element the element that needs to be added to the array.
     */
    public void add(JsonElement element) {
        getJsonArray().add(element);
    }

    /**
     * Adds all the elements of the specified array to self.
     *
     * @param array the array whose elements need to be added to the array.
     */
    public void addAll(JsonArray array) {
        getJsonArray().addAll(array);
    }

    /**
     * Replaces the element at the specified position in this array with the specified element.
     * Element can be null.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    public JsonElement set(int index, JsonElement element) {
        return getJsonArray().set(index, element);
    }

    /**
     * Removes the first occurrence of the specified element from this array, if it is present.
     * If the array does not contain the element, it is unchanged.
     *
     * @param element element to be removed from this array, if present
     * @return true if this array contained the specified element, false otherwise
     * @since 2.3
     */
    public boolean remove(JsonElement element) {
        return getJsonArray().remove(element);
    }

    /**
     * Removes the element at the specified position in this array. Shifts any subsequent elements
     * to the left (subtracts one from their indices). Returns the element that was removed from
     * the array.
     *
     * @param index index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     * @since 2.3
     */
    public JsonElement remove(int index) {
        return getJsonArray().remove(index);
    }

    /**
     * Returns true if this array contains the specified element.
     *
     * @param element whose presence in this array is to be tested
     * @return true if this array contains the specified element.
     * @since 2.3
     */
    public boolean contains(JsonElement element) {
        return getJsonArray().contains(element);
    }

    /**
     * Returns the number of elements in the array.
     *
     * @return the number of elements in the array.
     */
    public int size() {
        return getJsonArray().size();
    }

    /**
     * Returns true if the array is empty
     *
     * @return true if the array is empty
     */
    public boolean isEmpty() {
        return getJsonArray().isEmpty();
    }

    /**
     * Returns an iterator to navigate the elements of the array. Since the array is an ordered list,
     * the iterator navigates the elements in the order they were inserted.
     *
     * @return an iterator to navigate the elements of the array.
     */
    public Iterator<JsonElement> iterator() {
        return getJsonArray().iterator();
    }

    /**
     * Returns the ith element of the array.
     *
     * @param i the index of the element that is being sought.
     * @return the element present at the ith index.
     * @throws IndexOutOfBoundsException if <code>i</code> is negative or greater than or equal to the
     *                                   {@link #size()} of the array.
     */
    public JsonElement get(int i) {
        return getJsonArray().get(i);
    }

    /**
     * convenience method to get this array as a {@link Number} if it contains a single element.
     *
     * @return get this element as a number if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid Number.
     * @throws IllegalStateException if the array has more than one element.
     */
    public Number getAsNumber() {
        return getJsonArray().getAsNumber();
    }

    /**
     * convenience method to get this array as a {@link String} if it contains a single element.
     *
     * @return get this element as a String if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid String.
     * @throws IllegalStateException if the array has more than one element.
     */
    public String getAsString() {
        return getJsonArray().getAsString();
    }

    /**
     * convenience method to get this array as a double if it contains a single element.
     *
     * @return get this element as a double if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid double.
     * @throws IllegalStateException if the array has more than one element.
     */
    public double getAsDouble() {
        return getJsonArray().getAsDouble();
    }

    /**
     * convenience method to get this array as a {@link BigDecimal} if it contains a single element.
     *
     * @return get this element as a {@link BigDecimal} if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive}.
     * @throws NumberFormatException if the element at index 0 is not a valid {@link BigDecimal}.
     * @throws IllegalStateException if the array has more than one element.
     * @since 1.2
     */
    public BigDecimal getAsBigDecimal() {
        return getJsonArray().getAsBigDecimal();
    }

    /**
     * convenience method to get this array as a {@link BigInteger} if it contains a single element.
     *
     * @return get this element as a {@link BigInteger} if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive}.
     * @throws NumberFormatException if the element at index 0 is not a valid {@link BigInteger}.
     * @throws IllegalStateException if the array has more than one element.
     * @since 1.2
     */
    public BigInteger getAsBigInteger() {
        return getJsonArray().getAsBigInteger();
    }

    /**
     * convenience method to get this array as a float if it contains a single element.
     *
     * @return get this element as a float if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid float.
     * @throws IllegalStateException if the array has more than one element.
     */
    public float getAsFloat() {
        return getJsonArray().getAsFloat();
    }

    /**
     * convenience method to get this array as a long if it contains a single element.
     *
     * @return get this element as a long if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid long.
     * @throws IllegalStateException if the array has more than one element.
     */
    public long getAsLong() {
        return getJsonArray().getAsLong();
    }

    /**
     * convenience method to get this array as an integer if it contains a single element.
     *
     * @return get this element as an integer if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid integer.
     * @throws IllegalStateException if the array has more than one element.
     */
    public int getAsInt() {
        return getJsonArray().getAsInt();
    }

    /**
     * convenience method to get this element as a primitive byte value.
     *
     * @return get this element as a primitive byte value.
     * @throws ClassCastException    if the element is of not a {@link JsonPrimitive} and is not a valid
     *                               byte value.
     * @throws IllegalStateException if the element is of the type {@link JsonArray} but contains
     *                               more than a single element.
     * @since 1.3
     */
    public byte getAsByte() {
        return getJsonArray().getAsByte();
    }

    /**
     * convenience method to get the first character of this element as a string or the first
     * character of this array's first element as a string.
     *
     * @return the first character of the string.
     * @throws ClassCastException    if the element is of not a {@link JsonPrimitive} and is not a valid
     *                               string value.
     * @throws IllegalStateException if the element is of the type {@link JsonArray} but contains
     *                               more than a single element.
     * @since 1.3
     * @deprecated This method is misleading, as it does not get this element as a char but rather as
     * a string's first character.
     */
    @Deprecated
    public char getAsCharacter() {
        return getJsonArray().getAsCharacter();
    }

    /**
     * convenience method to get this array as a primitive short if it contains a single element.
     *
     * @return get this element as a primitive short if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid short.
     * @throws IllegalStateException if the array has more than one element.
     */
    public short getAsShort() {
        return getJsonArray().getAsShort();
    }

    /**
     * convenience method to get this array as a boolean if it contains a single element.
     *
     * @return get this element as a boolean if it is single element array.
     * @throws ClassCastException    if the element in the array is of not a {@link JsonPrimitive} and
     *                               is not a valid boolean.
     * @throws IllegalStateException if the array has more than one element.
     */
    public boolean getAsBoolean() {
        return getJsonArray().getAsBoolean();
    }

    /**
     * Creates a deep copy of this element and all its children
     */
    public JsonArray deepCopy() {
        return getJsonArray().deepCopy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        JsonArray other;
        if (this.getClass() == o.getClass()) {
            other = ((JsonArrayWrapper) o).getJsonArray();
        } else if (JsonArray.class == o.getClass()) {
            other = (JsonArray) o;
        } else {
            return false;
        }
        return getJsonArray().equals(other);
    }

    @Override
    public int hashCode() {
        return getJsonArray().hashCode();
    }
}
