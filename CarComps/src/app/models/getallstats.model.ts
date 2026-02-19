// ── Segéd interfészek ────────────────────────────────────────

export interface OrderModel {
  createdAt: string;
  isDeleted: boolean;
  id: number;
  userId: {
    id: number;
  };
  status: string;
  updatedAt: string;
}

export interface ActiveUserModel {
  lastName: string;
  lastLogin: string;
  role: string; // ← hiányzott a modellből
  isActive: boolean;
  createdAt: string;
  firstName: string;
  isSubscribed: boolean;
  isDeleted: boolean;
  phone: string;
  guid: string;
  id: number;
  email: string;
  updatedAt: string;
  username: string; // ← "usernmame" typo javítva
}

export interface MostPurchasedPartModel {
  quantity: number;
  partName: string; // ← "partname" → "partName" (nagy N)
}

// ── Fő modell ────────────────────────────────────────────────

export interface GetAllStatsModel {
  allRegisteredUserCount: number;
  ordersCount: number;
  pageViews: number;
  uniqueVisitors: number;
  allTransactions: string;
  allOrders: OrderModel[];
  allDeliveredOrders: OrderModel[];
  activeUsers: ActiveUserModel[];
  mostPurchasedPart: MostPurchasedPartModel[];
}

// ── API válasz wrapper ───────────────────────────────────────

export interface GetAllStatsResponse {
  result: GetAllStatsModel;
  status: string;
  statusCode: number;
}
