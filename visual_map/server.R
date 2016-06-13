library(shiny)
library(leaflet)
library(RColorBrewer)
library(shinythemes)

# setwd("~/Desktop/historical-map-ir/visual_map/R")
# install.packages("~/Downloads/htmltools_0.3.5.tgz", repos = NULL, type="source")

df_history <- read.csv("emap_A.csv")
df_history <- df_history[,c("name_tw","lat","lng","size_check","owner","years","website","address")]
df_history[,"size_check"] <- df_history[,"size_check"]/64

df_history[,"years"] <- as.numeric(gsub("([0-9]+).*$", "\\1", df_history[,"years"]))
df_history[which(is.na(df_history[,"years"])),"years"] <- 2000

m = leaflet(df_history) %>% addTiles()

owner <- unique(df_history[,"owner"])
RdYlBu = colorFactor("Spectral", owner)

# m %>% addCircleMarkers(~lng, ~lat, radius = ~size_check,
#                        color = ~RdYlBu(owner), fillOpacity = 0.5)

server <- function(input, output, session) {
  filteredData <- reactive({
    df_history[df_history$years >= input$range[1] & df_history$years <= input$range[2],]
  })
  
  output$map <- renderLeaflet({
    leaflet(df_history) %>% addTiles() %>%
      fitBounds(~min(lng), ~min(lat), ~max(lng), ~max(lat))
  })
  
  observe({
    proxy <- leafletProxy("map", data = df_history)
    proxy %>% clearControls()
    
    if (input$legend) {
      catego <- unique(df_history$owner)
      proxy %>% addLegend(position = "bottomright", colors = RdYlBu(catego),
                          labels = catego
      )
      
      leafletProxy("map", data = filteredData()) %>%
        clearShapes() %>% clearMarkerClusters() %>%
        addCircles(weight = ~size_check,
                   color = ~RdYlBu(owner),
                   opacity = 1,
                   fillOpacity = 1)
      
    }
    else{
      leafletProxy("map", data = filteredData()) %>%
        clearShapes() %>% clearMarkerClusters() %>%
        addMarkers(popup = ~paste("<h5>",name_tw,"</h5>",
                                  years,"年<br>",
                                  address,"<br>"),
                   clusterOptions = markerClusterOptions()) # %>%
      
    }
  })
}