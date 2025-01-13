import { ref } from "vue";
import { defineStore } from "pinia";

const locationStorage = defineStore("location", () => {
  const location = ref([]);

  const setLocation = (newLocation) => {
    location.value=newLocation;
  }

  const removeLocation = () => {
    location.value = [];
  }

  return { location, setLocation, removeLocation }
  
},{persist:true});

export default locationStorage;
