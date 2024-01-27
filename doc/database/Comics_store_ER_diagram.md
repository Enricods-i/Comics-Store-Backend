## Schema concettuale

erDiagram

    Personal_data {
        long id
        string first_name
        string last_name
        date birth_date
        string email
        string phone_number
        string city
        timestamp created_at
        timestamp modified_at
    }

    Cart_data {
        long id
        int size
        timestamp modified_at
    }

    Cart_content {
        int quantity
    }

    Wish_list {
        long id
        string name
        timestamp created_at
    }

    Comic {
        long id
        int number
        int quantity
        int pages
        string isbn
        date publication_date
        string description
        long version
        timestamp created_at
        timestamp modified_at
    }

    Author {
        long id
        string name
        string biography
        long version
        timestamp created_at
        timestamp modified_at
    }

    Collection {
        long id
        string name
        float price
        int year_of_release
        string format_and_binding
        boolean color
        string description
        long version
        timestamp created_at
        timestamp modified_at
    }

    Category {
        long id
        string name
        long version
        timestamp created_at
        timestamp modified_at
    }

    Discount {
        long id
        string name
        int percentage
        date activation_date
        date expiration_date
        long version
        timestamp created_at
        timestamp modified_at
    }

    Purchase {
        long id
        float total
        timestamp created_at
    }

    Comic_in_purchase {
        long id
        int comic_quantity
        float comic_price
    }

    Comic }o--o{ Discount : promotion
    Personal_data }o--|| Purchase : ""
    Collection }o--o{ Category : classification
    Cart_data ||--|| Personal_data : "user data"
    Comic ||--o{ Collection : ""
    Comic_in_purchase ||--|{ Purchase : ""
    Discount }o--o{ Comic_in_purchase : "discount application"
    Personal_data }o--|| Wish_list : ""
    Wish_list }o--o{ Comic : "list content"
    Comic }o--o{ Author : authors
    Comic_in_purchase ||--o{ Comic : ""
    Cart_data }o--|| Cart_content : ""
    Cart_content ||--o{ Comic : ""

