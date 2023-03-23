import { defineStore } from "pinia";
import {ref} from "vue";

// this is sidebar data s
// if your adding a router or link add data use object
// here

export const useSidebar = defineStore(() => {
  const username = ref('');

  // state: () => ({
  //   menu: [
  //     {
  //       name: "Dashboard",
  //       icon: "",
  //       type: "default",
  //       router: true,
  //       url: "",
  //     },
  //     {
  //       name: "Components",
  //       icon: "",
  //       type: "accordion",
  //       router: true,
  //       url: false,
  //       isChildren: true,
  //       children: [
  //         {
  //           name: "Alert",
  //           icon: false,
  //           type: "default",
  //         },
  //       ],
  //     },
  //   ],
  // }),
});
