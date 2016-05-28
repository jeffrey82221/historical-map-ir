library(leaflet)
# provide a data frame to leaflet()
categories = LETTERS[1:10]
df = data.frame(
  lat = rand_lat(100), lng = rand_lng(100), size = runif(100, 5, 20),
  category = factor(sample(categories, 100, replace = TRUE), levels = categories),
  value = rnorm(100)
)
m = leaflet(df) %>% addTiles()
m %>% addCircleMarkers(~lng, ~lat, radius = ~size)
m %>% addCircleMarkers(~lng, ~lat, radius = runif(100, 4, 10), color = c('red'))



# Discrete colors using the "RdYlBu" colorbrewer palette, mapped to categories
RdYlBu = colorFactor("RdYlBu", domain = categories)
m %>% addCircleMarkers(~lng, ~lat, radius = ~size,
                       color = ~RdYlBu(category), fillOpacity = 0.5)
