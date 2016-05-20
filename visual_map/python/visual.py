import folium
map_1 = folium.Map(location=[45.372, -121.6972],
                   zoom_start=12,
                   tiles='Stamen Terrain')
folium.Marker([45.3288, -121.6625],
              popup='Mt. Hood Meadows',
              icon=folium.Icon(icon='cloud')
             ).add_to(map_1)
folium.Marker([45.3311, -121.7113],
              popup='Timberline Lodge',
              icon=folium.Icon(color='green')
             ).add_to(map_1)
folium.Marker([45.3300, -121.6823],
              popup='Some Other Location',
              icon=folium.Icon(color='red',icon='info-sign')
              ).add_to(map_1)
map_1.save('demo.html')

import folium
import pandas as pd

state_geo = 'us-states.json'
state_unemployment = 'US_Unemployment_Oct2012.csv'

state_data = pd.read_csv(state_unemployment)

print(state_data)

#Let Folium determine the scale
map = folium.Map(location=[48, -102], zoom_start=3)
map.geo_json(geo_path=state_geo, data=state_data,
             columns=['State', 'Unemployment'],
             key_on='feature.id',
             fill_color='YlGn', fill_opacity=0.7, line_opacity=0.2,
             legend_name='Unemployment Rate (%)')
map.save('demo2.html')
