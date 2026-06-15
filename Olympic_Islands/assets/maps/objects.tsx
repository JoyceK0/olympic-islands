<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.1" name="objects" tilewidth="74" tileheight="113" tilecount="5" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <properties>
  <property name="atlasAsset" value="OBJECTS"/>
 </properties>
 <tile id="5" type="GameObject">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="animationSpeed" type="float" value="1"/>
   <property name="atlasAsset" value="OBJECTS"/>
   <property name="speed" type="float" value="4"/>
  </properties>
  <image source="objects/lucky.png" width="21" height="21"/>
  <objectgroup draworder="index" id="3">
   <object id="2" x="6.09906" y="16.4782" width="9.5231" height="4.70805">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="7" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/houseBig.png" width="74" height="113"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="4.31899" y="84.2093" width="64.9706" height="31.3701"/>
  </objectgroup>
 </tile>
 <tile id="8" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/horse.png" width="60" height="33"/>
  <objectgroup draworder="index" id="2">
   <object id="2" x="14.785" y="33.2335">
    <polygon points="0,0 26.2989,0.13084 33.3643,-6.28034 33.8877,-11.514 0.654202,-10.8598"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="9" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/tree.png" width="42" height="63"/>
  <objectgroup draworder="index" id="2">
   <object id="2" x="12.7066" y="55.1714">
    <polygon points="0,0 4.10213,-6.48887 4.10213,-10.2282 13.8072,-10.4482 13.9072,-7.25874 19.4101,-1.75969 19.31,2.19962 19.21,3.95931 0.300156,3.84933"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="10">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/stolk.png" width="15" height="34"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="1.64408" y="24.2744" width="11.702" height="9.7678"/>
  </objectgroup>
 </tile>
</tileset>
