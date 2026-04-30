export interface CarBrand {
  id: string;
  name: string;
}

export interface CarModel {
  id: string;
  name: string;
  brandId: string;
}

export interface CarYear {
  year: number;
  modelId: string;
}

// Filter output interface
export interface CarMMT {
  brandId: string;
  brandName: string;
  modelId: string;
  modelName: string;
  year: number;
}
