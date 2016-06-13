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
