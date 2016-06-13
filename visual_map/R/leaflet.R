library(shiny)
library(leaflet)
library(RColorBrewer)
library(shinythemes)

ui <- bootstrapPage(
        theme = shinytheme("cosmo"),
        title = "Taiwan Historical Map",
        tags$style(type = "text/css", "html, body {width:100%;height:100%}"),
        leafletOutput("map", width = "100%", height = "100%"),
        absolutePanel(top = 100, right = 30,
                      draggable = FALSE,
                      sliderInput("range", "Years", 1800, 2000,
                                  value = c(1800,2000), step = 10
                      ),
                      checkboxInput("legend", "Show Scatter Diagram", FALSE)
        ),
        absolutePanel(top = 20, right = 42,
                      titlePanel(h2("Taiwan Historical Map"))
        )
      )

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
                   opacity = 0.7,
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

shinyApp(ui, server)
