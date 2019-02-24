(ns pokemon.queries)

(def some-query
  "
query ($name: String!){
  someQuery(name: $name) {
    foo
  } 
}
")