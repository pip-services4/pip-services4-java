package org.pipservices4.data.random;

import java.util.*;
import java.util.stream.*;

/**
 * Random generator for various text values like names, addresses or phone numbers.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * String value1 = RandomText.name();     // Possible result: "Sergio"
 * String value2 = RandomText.verb();      // Possible result: "Run"
 * String value3 = RandomText.Text(50);    // Possible result: "Run jorge. Red high scream?"
 * }
 * </pre>
 */
public class RandomText {
    private static final String[] _namePrefixes = new String[]{"Dr.", "Mr.", "Mrs"};
    private static final String[] _nameSuffixes = new String[]{"Jr.", "Sr.", "II", "III"};
    private static final String[] _firstNames = new String[]{
            "John", "Bill", "Andrew", "Nick", "Pamela", "Bela", "Sergio", "George", "Hurry", "Cecilia", "Vesta", "Terry", "Patrick"
    };
    private static final String[] _lastNames = new String[]{
            "Doe", "Smith", "Johns", "Gates", "Carmack", "Zontak", "Clinton", "Adams", "First", "Lopez", "Due", "White", "Black"
    };
    private static final String[] _colors = new String[]{
            "Black", "White", "Red", "Blue", "Green", "Yellow", "Purple", "Grey", "Magenta", "Cian"
    };
    private static final String[] _stuffs = new String[]{
            "Game", "Ball", "Home", "Board", "Car", "Plane", "Hotel", "Wine", "Pants", "Boots", "Table", "Chair"
    };
    private static final String[] _adjectives = new String[]{
            "Large", "Small", "High", "Low", "Certain", "Fuzzy", "Modern", "Faster", "Slower"
    };
    private static final String[] _verbs = new String[]{
            "Run", "Stay", "Breeze", "Fly", "Lay", "Write", "Draw", "Scream"
    };
//    private static final String[] _streetTypes = new String[] {
//        "Lane", "Court", "Circle", "Drive", "Way", "Loop", "Blvd", "Street"
//    };
//    private static final String[] _streetPrefix = new String[] {
//        "North", "South", "East", "West", "Old", "New", "N.", "S.", "E.", "W."
//    };
//    private static final String[] _streetNames = new String[] {
//        "1st", "2nd", "3rd", "4th", "53rd", "6th", "8th", "Acacia", "Academy", "Adams", "Addison", "Airport", "Albany", "Alderwood", "Alton", "Amerige", "Amherst", "Anderson",
//        "Ann", "Annadale", "Applegate", "Arcadia", "Arch", "Argyle", "Arlington", "Armstrong", "Arnold", "Arrowhead", "Aspen", "Augusta", "Baker", "Bald Hill", "Bank", "Bay Meadows",
//        "Bay", "Bayberry", "Bayport", "Beach", "Beaver Ridge", "Bedford", "Beech", "Beechwood", "Belmont", "Berkshire", "Big Rock Cove", "Birch Hill", "Birchpond", "Birchwood",
//        "Bishop", "Blackburn", "Blue Spring", "Bohemia", "Border", "Boston", "Bow Ridge", "Bowman", "Bradford", "Brandywine", "Brewery", "Briarwood", "Brickell", "Brickyard",
//        "Bridge", "Bridgeton", "Bridle", "Broad", "Brookside", "Brown", "Buckingham", "Buttonwood", "Cambridge", "Campfire", "Canal", "Canterbury", "Cardinal", "Carpenter",
//        "Carriage", "Carson", "Catherine", "Cedar Swamp", "Cedar", "Cedarwood", "Cemetery", "Center", "Central", "Chapel", "Charles", "Cherry Hill", "Chestnut", "Church", "Circle",
//        "Clark", "Clay", "Cleveland", "Clinton", "Cobblestone", "Coffee", "College", "Colonial", "Columbia", "Cooper", "Corona", "Cottage", "Country Club", "Country", "County", "Court",
//        "Courtland", "Creek", "Creekside", "Crescent", "Cross", "Cypress", "Deerfield", "Del Monte", "Delaware", "Depot", "Devon", "Devonshire", "Division", "Dogwood", "Dunbar",
//        "Durham", "Eagle", "East", "Edgefield", "Edgemont", "Edgewater", "Edgewood", "El Dorado", "Elizabeth", "Elm", "Essex", "Euclid", "Evergreen", "Fairfield", "Fairground", "Fairview",
//        "Fairway", "Fawn", "Fifth", "Fordham", "Forest", "Foster", "Foxrun", "Franklin", "Fremont", "Front", "Fulton", "Galvin", "Garden", "Gartner", "Gates", "George", "Glen Creek",
//        "Glen Eagles", "Glen Ridge", "Glendale", "Glenlake", "Glenridge", "Glenwood", "Golden Star", "Goldfield", "Golf", "Gonzales", "Grand", "Grandrose", "Grant", "Green Hill",
//        "Green Lake", "Green", "Greenrose", "Greenview", "Gregory", "Griffin", "Grove", "Halifax", "Hamilton", "Hanover", "Harrison", "Hartford", "Harvard", "Harvey", "Hawthorne",
//        "Heather", "Henry Smith", "Heritage", "High Noon", "High Point", "High", "Highland", "Hill Field", "Hillcrest", "Hilldale", "Hillside", "Hilltop", "Holly", "Homestead",
//        "Homewood", "Honey Creek", "Howard", "Indian Spring", "Indian Summer", "Iroquois", "Jackson", "James", "Jefferson", "Jennings", "Jockey Hollow", "John", "Johnson", "Jones",
//        "Joy Ridge", "King", "Kingston", "Kirkland", "La Sierra", "Lafayette", "Lake Forest", "Lake", "Lakeshore", "Lakeview", "Lancaster", "Lane", "Laurel", "Leatherwood", "Lees Creek",
//        "Leeton Ridge", "Lexington", "Liberty", "Lilac", "Lincoln", "Linda", "Littleton", "Livingston", "Locust", "Longbranch", "Lookout", "Lower River", "Lyme", "Madison", "Maiden",
//        "Main", "Mammoth", "Manchester", "Manhattan", "Manor Station", "Maple", "Marconi", "Market", "Marsh", "Marshall", "Marvon", "Mayfair", "Mayfield", "Mayflower", "Meadow",
//        "Meadowbrook", "Mechanic", "Middle River", "Miles", "Mill Pond", "Miller", "Monroe", "Morris", "Mountainview", "Mulberry", "Myrtle", "Newbridge", "Newcastle", "Newport",
//        "Nichols", "Nicolls", "North", "Nut Swamp", "Oak Meadow", "Oak Valley", "Oak", "Oakland", "Oakwood", "Ocean", "Ohio", "Oklahoma", "Olive", "Orange", "Orchard", "Overlook",
//        "Pacific", "Paris Hill", "Park", "Parker", "Pawnee", "Peachtree", "Pearl", "Peg Shop", "Pendergast", "Peninsula", "Penn", "Pennington", "Pennsylvania", "Pheasant", "Philmont",
//        "Pierce", "Pin Oak", "Pine", "Pineknoll", "Piper", "Plumb Branch", "Poor House", "Prairie", "Primrose", "Prince", "Princess", "Princeton", "Proctor", "Prospect", "Pulaski",
//        "Pumpkin Hill", "Purple Finch", "Queen", "Race", "Ramblewood", "Redwood", "Ridge", "Ridgewood", "River", "Riverside", "Riverview", "Roberts", "Rock Creek", "Rock Maple",
//        "Rockaway", "Rockcrest", "Rockland", "Rockledge", "Rockville", "Rockwell", "Rocky River", "Roosevelt", "Rose", "Rosewood", "Ryan", "Saddle", "Sage", "San Carlos", "San Juan",
//        "San Pablo", "Santa Clara", "Saxon", "School", "Schoolhouse", "Second", "Shadow Brook", "Shady", "Sheffield", "Sherman", "Sherwood", "Shipley", "Shub Farm", "Sierra",
//        "Silver Spear", "Sleepy Hollow", "Smith Store", "Smoky Hollow", "Snake Hill", "Southampton", "Spring", "Spruce", "Squaw Creek", "St Louis", "St Margarets", "St Paul", "State",
//        "Stillwater", "Strawberry", "Studebaker", "Sugar", "Sulphur Springs", "Summerhouse", "Summit", "Sunbeam", "Sunnyslope", "Sunset", "Surrey", "Sutor", "Swanson", "Sycamore",
//        "Tailwater", "Talbot", "Tallwood", "Tanglewood", "Tarkiln Hill", "Taylor", "Thatcher", "Third", "Thomas", "Thompson", "Thorne", "Tower", "Trenton", "Trusel", "Tunnel",
//        "University", "Vale", "Valley Farms", "Valley View", "Valley", "Van Dyke", "Vermont", "Vernon", "Victoria", "Vine", "Virginia", "Wagon", "Wall", "Walnutwood", "Warren",
//        "Washington", "Water", "Wayne", "Westminster", "Westport", "White", "Whitemarsh", "Wild Rose", "William", "Williams", "Wilson", "Winchester", "Windfall", "Winding Way",
//        "Winding", "Windsor", "Wintergreen", "Wood", "Woodland", "Woodside", "Woodsman", "Wrangler", "York",
//    };

    private static final String[] _allWords = Stream.concat(Stream.concat(
                            Stream.concat(Arrays.stream(_firstNames), Arrays.stream(_lastNames)),
                            Stream.concat(Arrays.stream(_colors), Arrays.stream(_stuffs))),
                    Stream.concat(Arrays.stream(_adjectives), Arrays.stream(_verbs)))
            .toArray(String[]::new);

    /**
     * Generates a random color name.
     * The result value is capitalized.
     *
     * @return a random color name.
     */
    public static String color() {
        return RandomString.pick(_colors);
    }

    /**
     * Generates a random noun.
     * The result value is capitalized.
     *
     * @return a random noun.
     */
    public static String noun() {
        return RandomString.pick(_stuffs);
    }

    /**
     * Generates a random adjective.
     * The result value is capitalized.
     *
     * @return a random adjective.
     */
    public static String adjective() {
        return RandomString.pick(_adjectives);
    }

    /**
     * Generates a random verb.
     * The result value is capitalized.
     *
     * @return a random verb.
     */
    public static String verb() {
        return RandomString.pick(_verbs);
    }

    /**
     * Generates a random phrase which consists of few words separated by spaces.
     * The first word is capitalized, others are not.
     *
     * @param size the length of the phrase
     * @return a random phrase.
     */
    public static String phrase(int size) {
        return phrase(size, size);
    }

    /**
     * Generates a random phrase which consists of few words separated by spaces.
     * The first word is capitalized, others are not.
     *
     * @param minSize (optional) minimum string length.
     * @param maxSize maximum string length.
     * @return a random phrase.
     */
    public static String phrase(int minSize, int maxSize) {
        maxSize = Math.max(minSize, maxSize);
        int size = RandomInteger.nextInteger(minSize, maxSize);
        if (size <= 0) return "";

        StringBuilder result = new StringBuilder();
        result.append(RandomString.pick(_allWords));
        while (result.length() < size) {
            result.append(" ").append(RandomString.pick(_allWords).toLowerCase());
        }

        return result.toString();
    }

    /**
     * Generates a random person's name which has the following structure
     * optional prefix - first name - second name - optional suffix
     *
     * @return a random name.
     */
    public static String fullName() {
        StringBuilder result = new StringBuilder();

        if (RandomBoolean.chance(3, 5))
            result.append(RandomString.pick(_namePrefixes)).append(" ");

        result.append(RandomString.pick(_firstNames))
                .append(" ")
                .append(RandomString.pick(_lastNames));

        if (RandomBoolean.chance(5, 10))
            result.append(" ").append(RandomString.pick(_nameSuffixes));

        return result.toString();
    }

    /**
     * Generates a random word from available first names, last names, colors, stuffs, adjectives, or verbs.
     *
     * @return a random word.
     */
    public static String word() {
        return RandomString.pick(_allWords);
    }

    /**
     * Generates a random text that consists of random number of random words separated by spaces.
     *
     * @param min (optional) a minimum number of words.
     * @param max a maximum number of words.
     * @return a random text.
     */
    public static String words(int min, int max) {
        StringBuilder result = new StringBuilder();

        int count = RandomInteger.nextInteger(min, max);
        for (int i = 0; i < count; i++)
            result.append(RandomString.pick(_allWords));

        return result.toString();
    }

    /**
     * Generates a random phone number.
     * The phone number has the format: (XXX) XXX-YYYY
     *
     * @return a random phone number.
     */
    public static String phone() {
        return "(" +
                RandomInteger.nextInteger(111, 999) +
                ") " +
                RandomInteger.nextInteger(111, 999) +
                "-" +
                RandomInteger.nextInteger(0, 9999);
    }

    /**
     * Generates a random email address.
     *
     * @return a random email address.
     */
    public static String email() {
        return words(2, 6) + "@" + words(1, 3) + ".com";
    }

    /**
     * Generates a random text, consisting of first names, last names, colors, stuffs, adjectives, verbs, and punctuation marks.
     *
     * @param minSize minimum amount of words to generate. Text will contain 'minSize' words if 'maxSize' is omitted.
     * @param maxSize (optional) maximum amount of words to generate.
     * @return a random text.
     */
    public static String text(int minSize, int maxSize) {
        maxSize = Math.max(minSize, maxSize);
        int size = RandomInteger.nextInteger(minSize, maxSize);

        StringBuilder result = new StringBuilder();
        result.append(RandomString.pick(_allWords));

        while (result.length() < size) {
            String next = RandomString.pick(_allWords);
            if (RandomBoolean.chance(4, 6))
                next = " " + next.toLowerCase();
            else if (RandomBoolean.chance(2, 5))
                next = RandomString.pickChar(":,-") + next.toLowerCase();
            else if (RandomBoolean.chance(3, 5))
                next = RandomString.pickChar(":,-") + " " + next.toLowerCase();
            else
                next = RandomString.pickChar(".!?") + " " + next;

            result.append(next);
        }

        return result.toString();
    }

}
